package jetbrains.gis.tileprotocol

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.async.ThreadSafeAsync
import jetbrains.datalore.base.concurrent.Lock
import jetbrains.datalore.base.concurrent.execute
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.json.JsonSupport
import jetbrains.datalore.base.json.JsonSupport.formatJson
import jetbrains.datalore.base.registration.throwableHandlers.ThrowableHandlers
import jetbrains.gis.tileprotocol.Request.ConfigureConnectionRequest
import jetbrains.gis.tileprotocol.Request.GetBinaryGeometryRequest
import jetbrains.gis.tileprotocol.TileService.SocketStatus.*
import jetbrains.gis.tileprotocol.binary.ByteArrayStream
import jetbrains.gis.tileprotocol.binary.ResponseTileDecoder
import jetbrains.gis.tileprotocol.json.MapStyleJsonParser
import jetbrains.gis.tileprotocol.json.RequestFormatter
import jetbrains.gis.tileprotocol.mapConfig.MapConfig
import jetbrains.gis.tileprotocol.socket.SafeSocketHandler
import jetbrains.gis.tileprotocol.socket.Socket
import jetbrains.gis.tileprotocol.socket.SocketBuilder
import jetbrains.gis.tileprotocol.socket.SocketHandler


class TileService(socketBuilder: SocketBuilder, private val myTheme: String) {

    private val mySocket: Socket
    private val myMessageQueue = ArrayList<String>()
    private val pendingRequests = RequestMap()
    var mapConfig: MapConfig? = null
        private set
    private var myIncrement: Int = 0
    private var mySocketStatus = CLOSE

    init {
        mySocket = socketBuilder.build(SafeSocketHandler(TileSocketHandler(), ThrowableHandlers.instance))
    }


    fun getTileData(bbox: DoubleRectangle, zoom: Int): Async<List<TileLayer>> {
        val key = myIncrement++.toString()
        val async = ThreadSafeAsync<List<TileLayer>>()

        pendingRequests.put(key, async)

        try {
            sendGeometryRequest(RequestFormatter.format(GetBinaryGeometryRequest(key, zoom, bbox)).toString())
        } catch (err: Throwable) {
            pendingRequests.poll(key).failure(err)
        }

        return async
    }

    private fun sendGeometryRequest(messageString: String) {
        when (mySocketStatus) {
            OPEN -> mySocket.send(messageString)
            CONNECTING -> myMessageQueue.add(messageString)
            CLOSE -> {
                mySocket.connect()
                mySocketStatus = CONNECTING
                myMessageQueue.add(messageString)
            }
            ERROR -> throw IllegalStateException("Socket error")
        }
    }

    private fun sendInitMessage() {
        ConfigureConnectionRequest(myTheme)
            .run(RequestFormatter::format)
            .run(::formatJson)
            .run(mySocket::send)
    }

    private enum class SocketStatus {
        OPEN,
        CONNECTING,
        CLOSE,
        ERROR
    }

    inner class TileSocketHandler : SocketHandler {
        override fun onOpen() { mySocketStatus = OPEN; sendInitMessage() }
        override fun onClose() { mySocketStatus = CLOSE }
        override fun onError(cause: Throwable) { mySocketStatus = ERROR; failPending(cause) }

        override fun onTextMessage(message: String) {
            if (mapConfig == null) {
                mapConfig = MapStyleJsonParser.parse(JsonSupport.parseJson(message))
            }

            myMessageQueue.run { forEach(mySocket::send); clear() }
        }

        override fun onBinaryMessage(message: ByteArray) {
            try {
                val decoder = ResponseTileDecoder(ByteArrayStream(message))
                pendingRequests.poll(decoder.getKey()).success(decoder.getTileLayers())
            } catch (e: Throwable) {
                failPending(e)
            }
        }

        private fun failPending(cause: Throwable) { pendingRequests.pollAll().values.forEach { it.failure(cause) } }
    }

    class RequestMap {
        private val myAsyncMap = HashMap<String, ThreadSafeAsync<List<TileLayer>>>()

        fun put(key: String, async: ThreadSafeAsync<List<TileLayer>>) = Lock().execute {
            myAsyncMap[key] = async
        }

        fun pollAll(): Map<String, ThreadSafeAsync<List<TileLayer>>> = Lock().execute {
            return HashMap(myAsyncMap).also { myAsyncMap.clear() }
        }

        fun poll(key: String): ThreadSafeAsync<List<TileLayer>> = Lock().execute{
            return myAsyncMap.remove(key)!!
        }
    }
}
