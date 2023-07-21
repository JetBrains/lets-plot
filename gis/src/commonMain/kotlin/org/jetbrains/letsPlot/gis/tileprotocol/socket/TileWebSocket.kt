/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.gis.tileprotocol.socket

import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.launch

class TileWebSocket(
    private val myClient: HttpClient,
    private val myHandler: SocketHandler,
    private val myUrl: String
) : Socket {

    private var mySession: WebSocketSession? = null

    override fun connect() {
        with(myClient) {
            launch {
                webSocket(urlString = myUrl) {
                    mySession = this

                    myHandler.onOpen()

                    for (frame in incoming) {
                        when (frame) {
                            is Frame.Text -> myHandler.onTextMessage(frame.readText())
                            is Frame.Binary -> myHandler.onBinaryMessage(frame.readBytes())
                            else -> {} // ignore
                        }
                    }
                }
            }
        }
    }

    override fun close() {
        myClient.launch {
            mySession?.close(CloseReason(CloseReason.Codes.NORMAL, "Close session"))
            mySession = null
        }
    }

    override fun send(msg: String) {
        myClient.launch {

            mySession?.let {
                try {
                    it.outgoing.send(Frame.Text(msg))
                } catch (t: Throwable) {
                    myHandler.onClose(msg)
                }
            }
        }
    }
}
