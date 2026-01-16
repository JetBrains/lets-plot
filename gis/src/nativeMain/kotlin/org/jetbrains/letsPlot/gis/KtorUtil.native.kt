package org.jetbrains.letsPlot.gis

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import kotlinx.coroutines.*

actual fun newHttpClient(): io.ktor.client.HttpClient {
    return HttpClient(CIO)
}

actual fun newWebSocketClient(): HttpClient {
    return HttpClient(CIO) {
        install(WebSockets)
    }
}

actual fun newNetworkCoroutineScope(onException: (Throwable) -> Unit): CoroutineScope {
    val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable -> onException(throwable) }
    return CoroutineScope(SupervisorJob() + Dispatchers.IO + coroutineExceptionHandler)
}