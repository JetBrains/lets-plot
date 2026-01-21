package org.jetbrains.letsPlot.gis

import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import kotlinx.coroutines.*

actual fun newHttpClient(): HttpClient {
    return HttpClient()
}

actual fun newWebSocketClient(): HttpClient {
    return HttpClient() {
        install(WebSockets) {
            maxFrameSize = 10L * 1024 * 1024
        }

        engine {
            pipelining
        }
    }
}

actual fun newNetworkCoroutineScope(onException: (Throwable) -> Unit): CoroutineScope {
    val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable -> onException(throwable) }
    return CoroutineScope(SupervisorJob() + Dispatchers.IO + coroutineExceptionHandler)
}