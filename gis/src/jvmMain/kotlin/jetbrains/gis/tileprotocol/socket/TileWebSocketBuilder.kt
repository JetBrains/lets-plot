/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.gis.tileprotocol.socket

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.websocket.WebSockets
@kotlinx.coroutines.ObsoleteCoroutinesApi
actual class TileWebSocketBuilder actual constructor(
    private val myUrl: String
) : SocketBuilder {
    override fun build(handler: SocketHandler): Socket {
        val client = HttpClient(CIO) {
            install(WebSockets)
        }

        return TileWebSocket(client, handler, myUrl)
    }
}