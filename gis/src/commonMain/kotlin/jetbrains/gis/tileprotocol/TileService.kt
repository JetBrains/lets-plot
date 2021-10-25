/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.gis.tileprotocol

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.async.ThreadSafeAsync
import jetbrains.datalore.base.concurrent.Lock
import jetbrains.datalore.base.concurrent.execute
import jetbrains.datalore.base.json.JsonSupport
import jetbrains.datalore.base.json.JsonSupport.formatJson
import jetbrains.datalore.base.registration.throwableHandlers.ThrowableHandlers
import jetbrains.datalore.base.spatial.LonLat
import jetbrains.datalore.base.typedGeometry.Rect
import jetbrains.gis.tileprotocol.Request.ConfigureConnectionRequest
import jetbrains.gis.tileprotocol.Request.GetBinaryGeometryRequest
import jetbrains.gis.tileprotocol.TileService.SocketStatus.*
import jetbrains.gis.tileprotocol.binary.ResponseTileDecoder
import jetbrains.gis.tileprotocol.json.MapStyleJsonParser
import jetbrains.gis.tileprotocol.json.RequestFormatter
import jetbrains.gis.tileprotocol.mapConfig.MapConfig
import jetbrains.gis.tileprotocol.socket.SafeSocketHandler
import jetbrains.gis.tileprotocol.socket.SocketBuilder
import jetbrains.gis.tileprotocol.socket.SocketHandler

@kotlinx.coroutines.ObsoleteCoroutinesApi
open class TileService(socketBuilder: SocketBuilder, private val myTheme: Theme) {
    enum class Theme {
        COLOR,
        LIGHT,
        DARK
    }

    private val mySocket = socketBuilder.build(SafeSocketHandler(TileSocketHandler(), ThrowableHandlers.instance))
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
