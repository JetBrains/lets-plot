package org.jetbrains.letsPlot.gis

import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

actual fun newHttpClient(): io.ktor.client.HttpClient {
    return HttpClient()
}

actual fun newWebSocketClient(): HttpClient {
    return HttpClient() {
        install(WebSockets)
    }
}

actual fun newNetworkCoroutineScope(onException: (Throwable) -> Unit): CoroutineScope {
    val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable -> onException(throwable) }
    return CoroutineScope(SupervisorJob() + Dispatchers.Default + coroutineExceptionHandler)
}
