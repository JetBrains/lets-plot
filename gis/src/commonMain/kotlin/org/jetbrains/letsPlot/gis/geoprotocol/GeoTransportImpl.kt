/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.gis.geoprotocol

import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.coroutines.launch
import org.jetbrains.letsPlot.commons.intern.async.Async
import org.jetbrains.letsPlot.commons.intern.async.ThreadSafeAsync
import org.jetbrains.letsPlot.commons.intern.json.JsonSupport
import org.jetbrains.letsPlot.gis.geoprotocol.json.RequestJsonFormatter
import org.jetbrains.letsPlot.gis.geoprotocol.json.ResponseJsonParser
import org.jetbrains.letsPlot.gis.newHttpClient
import org.jetbrains.letsPlot.gis.newNetworkCoroutineScope

// Never create HttpClient in common code - it causes type mismatch when run inside an IntelliJ Plugin:
// java.util.ServiceConfigurationError: io.ktor.client.HttpClientEngineContainer: io.ktor.client.engine.java.JavaHttpEngineContainer not a subtype

class GeoTransportImpl(
    private val myUrl: String
): GeoTransport {
    private val client = newHttpClient()
    private val coroutineScope = newNetworkCoroutineScope()

    override fun send(request: GeoRequest): Async<GeoResponse> {
        val async = ThreadSafeAsync<GeoResponse>()

        coroutineScope.launch {
            try {
                val response = client.post(myUrl) {
                    setBody(request
                        .let(RequestJsonFormatter::format)
                        .let(JsonSupport::formatJson)
                    )
                }

                response
                    .body<String>()
                    .let(JsonSupport::parseJson)
                    .let(ResponseJsonParser::parse)
                    .let(async::success)

            } catch (c: Throwable) {
                async.failure(c)
            }
        }

        return async
    }
}
