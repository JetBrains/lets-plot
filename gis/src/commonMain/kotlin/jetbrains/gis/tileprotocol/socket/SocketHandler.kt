package jetbrains.gis.tileprotocol.socket

interface SocketHandler {
    fun onClose(message: String)
    fun onError(cause: Throwable)
    fun onTextMessage(message: String)
    fun onBinaryMessage(message: ByteArray)
    fun onOpen()
}