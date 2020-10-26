/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.gis.geoprotocol

import io.ktor.client.HttpClient
import io.ktor.client.request.post
import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.async.ThreadSafeAsync
import jetbrains.datalore.base.json.JsonSupport
import jetbrains.datalore.base.json.JsonSupport.parseJson
import jetbrains.gis.geoprotocol.json.RequestJsonFormatter
import jetbrains.gis.geoprotocol.json.RequestJsonFormatter.format
import jetbrains.gis.geoprotocol.json.ResponseJsonParser
import jetbrains.gis.geoprotocol.json.ResponseJsonParser.parse
import kotlinx.coroutines.launch

class GeoTransportImpl(
    private val myUrl: String
): GeoTransport {
    private val myClient = HttpClient()

    override fun send(request: GeoRequest): Async<GeoResponse> {
        val async = ThreadSafeAsync<GeoResponse>()

        myClient.launch {
            try {
                val response = myClient.post<String>(myUrl) {
                    body = request
                        .let(RequestJsonFormatter::format)
                        .let(JsonSupport::formatJson)
                }

                response
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
