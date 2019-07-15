package jetbrains.gis.tileprotocol.socket

interface SocketBuilder {
    fun build(handler: SocketHandler): Socket

    abstract class BaseSocketBuilder protected constructor(val url: String) : SocketBuilder
}
