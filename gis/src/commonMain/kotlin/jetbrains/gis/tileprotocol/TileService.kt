package jetbrains.gis.tileprotocol

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.async.ThreadSafeAsync
import jetbrains.datalore.base.concurrent.Lock
import jetbrains.datalore.base.concurrent.execute
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.json.JsonSupport
import jetbrains.datalore.base.registration.throwableHandlers.ThrowableHandlers
import jetbrains.gis.tileprotocol.Request.ConfigureConnectionRequest
import jetbrains.gis.tileprotocol.Request.GetBinaryGeometryRequest
import jetbrains.gis.tileprotocol.binary.ByteArrayStream
import jetbrains.gis.tileprotocol.binary.ResponseTileDecoder
import jetbrains.gis.tileprotocol.json.MapStyleJsonParser
import jetbrains.gis.tileprotocol.json.RequestJsonFormatter
import jetbrains.gis.tileprotocol.mapConfig.MapConfig
import jetbrains.gis.tileprotocol.socket.SafeSocketHandler
import jetbrains.gis.tileprotocol.socket.Socket
import jetbrains.gis.tileprotocol.socket.SocketBuilder
import jetbrains.gis.tileprotocol.socket.SocketHandler


class TileService(socketBuilder: SocketBuilder, private val myTheme: String) {

    private val mySocket: Socket
    private val myMessageQueue = ArrayList<String>()
    private val requestMap = RequestMap()
    var mapConfig: MapConfig? = null
        private set
    private var myIncrement: Int = 0
    private var mySocketStatus = SocketStatus.CLOSE

    init {
        mySocket = socketBuilder.build(SafeSocketHandler(TileSocketHandler(), ThrowableHandlers.instance))
    }


    fun getTileData(bbox: DoubleRectangle, zoom: Int): Async<List<TileLayer>> {
        val key = myIncrement++.toString()
        val async = ThreadSafeAsync<List<TileLayer>>()

        requestMap.put(key, async)

        try {
            send(RequestJsonFormatter.format(GetBinaryGeometryRequest(key, zoom, bbox)).toString())
        } catch (err: Throwable) {
            requestMap.poll(key).failure(err)
        }

        return async
    }

    private fun send(messageString: String) {
        when (mySocketStatus) {
            SocketStatus.OPEN -> mySocket.send(messageString)
            SocketStatus.CONNECTING -> myMessageQueue.add(messageString)
            SocketStatus.CLOSE -> {
                mySocket.connect()
                mySocketStatus = SocketStatus.CONNECTING
                myMessageQueue.add(messageString)
            }
            SocketStatus.ERROR -> throw IllegalStateException("Socket error")
        }
    }

    private fun sendInitMessage() {
        mySocket.send(RequestJsonFormatter.format(ConfigureConnectionRequest(myTheme)).toString())
    }

    private enum class SocketStatus {
        OPEN,
        CONNECTING,
        CLOSE,
        ERROR
    }

    inner class TileSocketHandler : SocketHandler {
        override fun onClose() { mySocketStatus = SocketStatus.CLOSE }

        override fun onError(cause: Throwable) {
            mySocketStatus = SocketStatus.ERROR
            failAllAsyncs(cause)
        }

        override fun onTextMessage(message: String) {
            if (mapConfig == null) {
                mapConfig = MapStyleJsonParser.parse(JsonSupport.parseJson(message))
            }

            myMessageQueue.forEach { mySocket.send(it) }
            myMessageQueue.clear()
        }

        override fun onBinaryMessage(message: ByteArray) {
            try {
                val decoder = ResponseTileDecoder(ByteArrayStream(message))
                requestMap.poll(decoder.getKey()).success(decoder.getTileLayers())
            } catch (e: Throwable) {
                failAllAsyncs(e)
            }
        }

        override fun onOpen() {
            mySocketStatus = SocketStatus.OPEN
            sendInitMessage()
        }

        private fun failAllAsyncs(cause: Throwable) { requestMap.pollAll().values.forEach { it.failure(cause) } }
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
