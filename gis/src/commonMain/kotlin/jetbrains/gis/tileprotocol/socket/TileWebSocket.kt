/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.gis.tileprotocol.socket

import io.ktor.client.HttpClient
import io.ktor.client.features.websocket.wss
import io.ktor.http.DEFAULT_PORT
import io.ktor.http.HttpMethod
import io.ktor.http.cio.websocket.*
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.launch

@ObsoleteCoroutinesApi
@KtorExperimentalAPI
class TileWebSocket(
    private val myClient: HttpClient,
    private val myHandler: SocketHandler,
    private val myHost: String,
    private val myPort: Int?
) : Socket {

    private var mySession: WebSocketSession? = null

    override fun connect() {
        with(myClient) {
            launch {
                wss(
                    method = HttpMethod.Get,
                    host = myHost,
                    port = myPort ?: DEFAULT_PORT
                ) {
                    mySession = this

                    myHandler.onOpen()

                    for (frame in incoming) {
                        when (frame) {
                            is Frame.Text -> myHandler.onTextMessage(frame.readText())
                            is Frame.Binary -> myHandler.onBinaryMessage(frame.readBytes())
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