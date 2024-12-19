/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.gis.tileprotocol.socket

import io.ktor.client.*
import io.ktor.client.engine.js.*
import io.ktor.client.plugins.websocket.*

actual class TileWebSocketBuilder
actual constructor(
    private val myUrl: String
) : SocketBuilder {
    actual override fun build(handler: SocketHandler): Socket {
        val client = HttpClient(Js) {
            install(WebSockets)
        }

        return TileWebSocket(client, handler, myUrl)
    }
}