package jetbrains.gis.tileprotocol.socket

interface Socket {
    fun connect()
    fun close()
    fun send(msg: String)
}
