/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.gis.tileprotocol.socket

import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.launch
import org.jetbrains.letsPlot.gis.newNetworkCoroutineScope
import org.jetbrains.letsPlot.gis.newWebSocketClient

class TileWebSocket(
    private val myUrl: String,
    private val myHandler: SocketHandler
)  {
    private val client: HttpClient = newWebSocketClient()
    private val coroutineScope = newNetworkCoroutineScope()
    private var session: WebSocketSession? = null

    fun connect() {
        coroutineScope.launch {
            try {
                client.webSocket(urlString = myUrl) {
                    session = this

                    myHandler.onOpen()

                    for (frame in incoming) {
                        when (frame) {
                            is Frame.Text -> myHandler.onTextMessage(frame.readText())
                            is Frame.Binary -> myHandler.onBinaryMessage(frame.readBytes())
                            else -> {} // ignore
                        }
                    }
                }
            } catch (ex: Exception) {
                println("TileWebSocket.connect() failed: ${ex.message}")
                ex.printStackTrace()
            }
        }
    }

    fun close() {
        coroutineScope.launch {
            session?.close(CloseReason(CloseReason.Codes.NORMAL, "Close session"))
            session = null
        }
    }

    fun send(msg: String) {
        coroutineScope.launch {
            session?.let {
                try {
                    it.outgoing.send(Frame.Text(msg))
                } catch (t: Throwable) {
                    myHandler.onClose(msg)
                }
            }
        }
    }
}
