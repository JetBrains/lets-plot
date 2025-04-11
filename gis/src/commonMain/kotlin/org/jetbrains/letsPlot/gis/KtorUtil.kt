/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.gis

import io.ktor.client.*
import kotlinx.coroutines.CoroutineScope

expect fun newHttpClient(): HttpClient
expect fun newWebSocketClient(): HttpClient
expect fun newNetworkCoroutineScope(onException: (Throwable) -> Unit = Throwable::printStackTrace): CoroutineScope
