/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.gis.geoprotocol

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import org.jetbrains.letsPlot.base.intern.async.Async
import org.jetbrains.letsPlot.base.intern.async.ThreadSafeAsync
import jetbrains.datalore.base.json.JsonSupport
import jetbrains.gis.geoprotocol.json.RequestJsonFormatter
import jetbrains.gis.geoprotocol.json.ResponseJsonParser
import kotlinx.coroutines.launch

class GeoTransportImpl(
    private val myUrl: String
): GeoTransport {
    private val myClient = HttpClient()

    override fun send(request: GeoRequest): Async<GeoResponse> {
        val async = ThreadSafeAsync<GeoResponse>()

        myClient.launch {
            try {
                val response = myClient.post(myUrl) {
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
