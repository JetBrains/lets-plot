package jetbrains.gis.tileprotocol.socket

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.websocket.WebSockets
import io.ktor.util.KtorExperimentalAPI

actual class TileWebSocketBuilder actual constructor(
    private val myHost: String,
    private val myPort: Int?
) : SocketBuilder {
    @KtorExperimentalAPI
    override fun build(handler: SocketHandler): Socket {
        val client = HttpClient(CIO) {
            install(WebSockets)
        }

        return TileWebSocket(client, handler, myHost, myPort)
    }
}