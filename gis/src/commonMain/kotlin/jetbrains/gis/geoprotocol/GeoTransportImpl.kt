package jetbrains.gis.geoprotocol

import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.http.DEFAULT_PORT
import io.ktor.http.URLProtocol
import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.async.ThreadSafeAsync
import jetbrains.datalore.base.json.JsonSupport
import jetbrains.datalore.base.json.JsonSupport.parseJson
import jetbrains.gis.geoprotocol.json.RequestJsonFormatter.format
import jetbrains.gis.geoprotocol.json.ResponseJsonParser.parse
import kotlinx.coroutines.launch

class GeoTransportImpl(private val myHost: String, private val myPort: Int?, private val myRoute: String): GeoTransport {
    private val myClient = HttpClient()

    override fun send(request: GeoRequest): Async<GeoResponse> {
        val async = ThreadSafeAsync<GeoResponse>()

        myClient.launch {
            try {
                val response= myClient.post<String> {
                    url {
                        protocol = URLProtocol.HTTPS
                        host = myHost
                        port = myPort ?: DEFAULT_PORT
                        encodedPath = myRoute
                    }

                    body = JsonSupport.formatJson(format(request))
                }

                async.success(parse(parseJson(response)))
            } catch (c: Throwable) {
                async.failure(c)
            }
        }

        return async
    }
}
