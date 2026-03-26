/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.mapengine.basemap.raster

import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import org.jetbrains.letsPlot.commons.intern.async.Async
import org.jetbrains.letsPlot.commons.intern.async.ThreadSafeAsync
import org.jetbrains.letsPlot.gis.newHttpClient
import org.jetbrains.letsPlot.gis.newNetworkCoroutineScope
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class HttpTileTransport {
    private val client = newHttpClient()
    private val coroutineScope = newNetworkCoroutineScope()

    fun get(url: String, timeout: Duration = 5.seconds): Async<ByteArray> {
        val async = ThreadSafeAsync<ByteArray>()

        coroutineScope.launch {
            try {
                val response = withTimeout(timeout) {
                    client.request(url) {
                        method = HttpMethod("GET")
                        expectSuccess = true
                    }.readRawBytes()
                }

                async.success(response)
            } catch (c: ResponseException) {
                async.failure(Exception(c.response.status.toString()))
            } catch (c: Throwable) {
                async.failure(c)
            }
        }

        return async
    }
}