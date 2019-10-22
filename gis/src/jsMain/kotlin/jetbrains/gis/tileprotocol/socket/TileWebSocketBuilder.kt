package jetbrains.gis.tileprotocol.socket

import io.ktor.client.HttpClient
import io.ktor.client.engine.js.Js
import io.ktor.client.features.websocket.WebSockets
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.ObsoleteCoroutinesApi

actual class TileWebSocketBuilder
actual constructor(
    private val myHost: String,
    private val myPort: Int?
) : SocketBuilder {
    @ObsoleteCoroutinesApi
    @KtorExperimentalAPI
    override fun build(handler: SocketHandler): Socket {
        val client = HttpClient(Js) {
            install(WebSockets)
        }

        return TileWebSocket(client, handler,myHost, myPort)
    }
}