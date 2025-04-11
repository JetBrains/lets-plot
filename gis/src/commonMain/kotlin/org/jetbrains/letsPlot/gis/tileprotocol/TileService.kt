/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.gis.tileprotocol

import org.jetbrains.letsPlot.commons.intern.async.Async
import org.jetbrains.letsPlot.commons.intern.async.ThreadSafeAsync
import org.jetbrains.letsPlot.commons.intern.concurrent.Lock
import org.jetbrains.letsPlot.commons.intern.concurrent.execute
import org.jetbrains.letsPlot.commons.intern.json.JsonSupport
import org.jetbrains.letsPlot.commons.intern.json.JsonSupport.formatJson
import org.jetbrains.letsPlot.commons.intern.spatial.LonLat
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Rect
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

open class TileService(url: String, private val myTheme: Theme) {
    enum class Theme {
        COLOR,
        LIGHT,
        DARK,
        BW
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

        pendingRequests.put(key, async)

        try {
            GetBinaryGeometryRequest(key, zoom, bbox)
                .run(RequestFormatter::format)
                .run(JsonSupport::formatJson)
                .run(this::sendGeometryRequest)

        } catch (err: Throwable) {
            pendingRequests.poll(key).failure(err)
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
            if (mapConfig == null) {
                mapConfig = MapStyleJsonParser.parse(JsonSupport.parseJson(message))
            }
            myStatus = CONFIGURED
            myMessageQueue.run { forEach(mySocket::send); clear() }
        }

        override fun onBinaryMessage(message: ByteArray) {
            try {
                ResponseTileDecoder(message)
                    .let { (key, tiles) -> pendingRequests.poll(key).success(tiles) }
            } catch (e: Throwable) {
                failPending(e)
            }
        }

        private fun failPending(cause: Throwable) {
            pendingRequests.pollAll().values.forEach { it.failure(cause) }
        }
    }

    class RequestMap {
        private val lock = Lock()
        private val myAsyncMap = HashMap<String, ThreadSafeAsync<List<TileLayer>>>()

        fun put(key: String, async: ThreadSafeAsync<List<TileLayer>>) = lock.execute {
            myAsyncMap[key] = async
        }

        fun pollAll(): Map<String, ThreadSafeAsync<List<TileLayer>>> = lock.execute {
            return HashMap(myAsyncMap).also { myAsyncMap.clear() }
        }

        fun poll(key: String): ThreadSafeAsync<List<TileLayer>> = lock.execute {
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
