/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.gis

import io.ktor.client.*
import io.ktor.client.engine.js.*
import io.ktor.client.plugins.websocket.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

actual fun newHttpClient(): HttpClient {
    return HttpClient(Js)
}

actual fun newWebSocketClient(): HttpClient {
    return HttpClient(Js) {
        install(WebSockets)
    }
}

actual fun newNetworkCoroutineScope(onException: (Throwable) -> Unit): CoroutineScope {
    val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable -> onException(throwable) }
    return CoroutineScope(SupervisorJob() + Dispatchers.Default + coroutineExceptionHandler)
}
