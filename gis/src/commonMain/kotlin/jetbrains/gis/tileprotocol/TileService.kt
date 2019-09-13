package jetbrains.gis.tileprotocol

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.async.ThreadSafeAsync
import jetbrains.datalore.base.concurrent.Lock
import jetbrains.datalore.base.concurrent.execute
import jetbrains.datalore.base.json.JsonSupport
import jetbrains.datalore.base.json.JsonSupport.formatJson
import jetbrains.datalore.base.projectionGeometry.LonLatRectangle
import jetbrains.datalore.base.registration.throwableHandlers.ThrowableHandlers
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


open class TileService(socketBuilder: SocketBuilder, private val myTheme: String) {

    private val mySocket = socketBuilder.build(SafeSocketHandler(TileSocketHandler(), ThrowableHandlers.instance))
    private val myMessageQueue = ArrayList<String>()
    private val pendingRequests = RequestMap()
    var mapConfig: MapConfig? = null
        private set
    private var myIncrement: Int = 0
    private var mySocketStatus = NOT_CONNECTED

    open fun getTileData(bbox: LonLatRectangle, zoom: Int): Async<List<TileLayer>> {
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

    private fun sendGeometryRequest(messageString: String) {
        when (mySocketStatus) {
            NOT_CONNECTED -> {
                myMessageQueue.add(messageString)
                mySocketStatus = CONNECTING
                mySocket.connect()
            }
            OPEN -> mySocket.send(messageString)
            CONNECTING -> myMessageQueue.add(messageString)
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
        NOT_CONNECTED,
        OPEN,
        CONNECTING,
        ERROR
    }

    inner class TileSocketHandler : SocketHandler {
        override fun onOpen() { mySocketStatus = OPEN; sendInitMessage() }
        override fun onClose(message: String) {
            myMessageQueue.add(message)
            if (mySocketStatus == OPEN) {
                mySocketStatus = CONNECTING
                mySocket.connect()
            }
        }
        override fun onError(cause: Throwable) { mySocketStatus = ERROR; failPending(cause) }

        override fun onTextMessage(message: String) {
            if (mapConfig == null) {
                mapConfig = MapStyleJsonParser.parse(JsonSupport.parseJson(message))
            }

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

        private fun failPending(cause: Throwable) { pendingRequests.pollAll().values.forEach { it.failure(cause) } }
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

        fun poll(key: String): ThreadSafeAsync<List<TileLayer>> = lock.execute{
            return myAsyncMap.remove(key)!!
        }
    }
}
