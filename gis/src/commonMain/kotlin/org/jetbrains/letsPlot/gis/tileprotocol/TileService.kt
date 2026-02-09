/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */


package org.jetbrains.letsPlot.gis.tileprotocol

import kotlinx.io.buffered
import kotlinx.io.files.FileSystem
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.writeString
import org.jetbrains.letsPlot.commons.encoding.Base64
import org.jetbrains.letsPlot.commons.formatting.string.ByteSizeFormatter.formatByteSize
import org.jetbrains.letsPlot.commons.intern.async.Async
import org.jetbrains.letsPlot.commons.intern.async.ThreadSafeAsync
import org.jetbrains.letsPlot.commons.intern.concurrent.Lock
import org.jetbrains.letsPlot.commons.intern.concurrent.execute
import org.jetbrains.letsPlot.commons.intern.json.JsonSupport
import org.jetbrains.letsPlot.commons.intern.json.JsonSupport.formatJson
import org.jetbrains.letsPlot.commons.intern.spatial.LonLat
import org.jetbrains.letsPlot.commons.intern.typedGeometry.*
import org.jetbrains.letsPlot.gis.tileprotocol.Request.ConfigureConnectionRequest
import org.jetbrains.letsPlot.gis.tileprotocol.Request.GetBinaryGeometryRequest
import org.jetbrains.letsPlot.gis.tileprotocol.TileService.SocketStatus.*
import org.jetbrains.letsPlot.gis.tileprotocol.binary.ResponseTileDecoder
import org.jetbrains.letsPlot.gis.tileprotocol.json.MapStyleJsonParser
import org.jetbrains.letsPlot.gis.tileprotocol.json.RequestFormatter
import org.jetbrains.letsPlot.gis.tileprotocol.mapConfig.MapConfig
import org.jetbrains.letsPlot.gis.tileprotocol.socket.SafeSocketHandler
import org.jetbrains.letsPlot.gis.tileprotocol.socket.SocketHandler
import org.jetbrains.letsPlot.gis.tileprotocol.socket.TileWebSocket

val fileSystem: FileSystem = SystemFileSystem

open class TileService(url: String, private val myTheme: Theme) {
    enum class Theme {
        COLOR,
        LIGHT,
        DARK,
        BW
    }

    private val logEnabled = false
    private fun log(message: () -> String) {
        if (logEnabled) {
            println(message())
        }
    }

    private val mySocket: TileWebSocket = TileWebSocket(url, SafeSocketHandler(TileSocketHandler()))
    private val myMessageQueue = ThreadSafeMessageQueue<String>()
    private val pendingRequests = RequestMap()
    var mapConfig: MapConfig? = null
        private set
    private var myIncrement: Int = 0
    private var myStatus = NOT_CONNECTED

    open fun getTileData(bbox: Rect<LonLat>, zoom: Int): Async<List<TileLayer>> {
        val key = myIncrement++.toString()
        val async = ThreadSafeAsync<List<TileLayer>>()

        pendingRequests.put(key, bbox to async)

        try {
            GetBinaryGeometryRequest(key, zoom, bbox)
                .run(RequestFormatter::format)
                .run(JsonSupport::formatJson)
                .run(this::sendGeometryRequest)

        } catch (err: Throwable) {
            pendingRequests.poll(key).second.failure(err)
        }

        return async
    }

    private fun sendGeometryRequest(message: String) {
        when (myStatus) {
            NOT_CONNECTED -> {
                myMessageQueue.add(message)
                myStatus = CONNECTING
                mySocket.connect()
            }

            CONFIGURED -> mySocket.send(message)
            CONNECTING -> myMessageQueue.add(message)
            ERROR -> throw IllegalStateException("Socket error")
        }
    }

    private fun sendInitMessage() {
        ConfigureConnectionRequest(myTheme.name.lowercase())
            .run(RequestFormatter::format)
            .run(::formatJson)
            .run(mySocket::send)
    }

    private enum class SocketStatus {
        NOT_CONNECTED,
        CONFIGURED,
        CONNECTING,
        ERROR
    }

    inner class TileSocketHandler : SocketHandler {
        override fun onOpen() {
            sendInitMessage()
        }

        override fun onClose(message: String) {
            myMessageQueue.add(message)
            if (myStatus == CONFIGURED) {
                myStatus = CONNECTING
                mySocket.connect()
            }
        }

        override fun onError(cause: Throwable) {
            myStatus = ERROR; failPending(cause)
        }

        override fun onTextMessage(message: String) {
            //println("TileService: received text message $message")
            if (mapConfig == null) {
                mapConfig = MapStyleJsonParser.parse(JsonSupport.parseJson(message))
            }
            myStatus = CONFIGURED
            myMessageQueue.run { forEach(mySocket::send); clear() }
        }

        override fun onBinaryMessage(message: ByteArray) {
            try {
                ResponseTileDecoder(message)
                    .let { (key, tiles) ->
                        val (rect, async) = pendingRequests.poll(key)
                        log { "$rect -> ${formatByteSize(message.size)}" }

                        try {
                            // Open a sink to the file, and buffer it
                            val className =
                                "TileX${rect.origin.x}Y${rect.origin.y}W${rect.dimension.x}H${rect.dimension.y}"
                                    .replace('.', '_')
                                    .replace('-', 'm')
                            val fileName = "$className.kt"
                            val path = Path(fileName)
                            SystemFileSystem.sink(path).buffered().use { sink ->
                                sink.writeString(
                                    """|import org.jetbrains.letsPlot.commons.intern.spatial.LonLat
                                   |import org.jetbrains.letsPlot.commons.intern.typedGeometry.Rect
                                   |import org.jetbrains.letsPlot.commons.encoding.Base64
                                   |
                                   |
                                   |object $className {
                                   |    val rect = Rect.XYWH<LonLat>(${rect.left}, ${rect.top}, ${rect.width}, ${rect.height})
                                   |    val data = ${"\"\"\""}${Base64.encode(message)}${"\"\"\""}
                                   |    val entry = rect to Base64.decode(data)
                                   |}""".trimMargin()
                                )
                            }

                            log { SystemFileSystem.resolve(path).toString() }
                        } catch (err: Throwable) {
                            log { "Failed to write tile data to file: ${err.message}" }
                        }

                        async.success(tiles)
                    }
            } catch (e: Throwable) {
                failPending(e)
            }
        }

        private fun failPending(cause: Throwable) {
            pendingRequests.pollAll().values.forEach { it.second.failure(cause) }
        }
    }

    class RequestMap {
        private val lock = Lock()
        private val myAsyncMap = HashMap<String, Pair<Rect<LonLat>, ThreadSafeAsync<List<TileLayer>>>>()

        fun put(key: String, async: Pair<Rect<LonLat>, ThreadSafeAsync<List<TileLayer>>>) = lock.execute {
            myAsyncMap[key] = async
        }

        fun pollAll(): Map<String, Pair<Rect<LonLat>, ThreadSafeAsync<List<TileLayer>>>> = lock.execute {
            return HashMap(myAsyncMap).also { myAsyncMap.clear() }
        }

        fun poll(key: String): Pair<Rect<LonLat>, ThreadSafeAsync<List<TileLayer>>> = lock.execute {
            return myAsyncMap.remove(key)!!
        }
    }

    class ThreadSafeMessageQueue<T> {
        private val myList = ArrayList<T>()
        private val myLock = Lock()

        fun add(v: T) {
            myLock.execute {
                myList.add(v)
            }
        }

        fun forEach(f: (T) -> Unit) {
            myLock.execute {
                myList.forEach(f)
            }
        }

        fun clear() {
            myLock.execute {
                myList.clear()
            }
        }
    }
}
