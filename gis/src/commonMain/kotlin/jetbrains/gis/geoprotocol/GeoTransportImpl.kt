package jetbrains.gis.geoprotocol

import io.ktor.client.HttpClient
import io.ktor.client.call.call
import io.ktor.client.request.header
import io.ktor.http.HttpMethod
import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.async.ThreadSafeAsync
import jetbrains.gis.geoprotocol.json.RequestJsonFormatter.format
import kotlinx.coroutines.launch

class GeoTransportImpl(private val myHost: String): GeoTransport {
    private val myClient = HttpClient()

    override fun send(request: GeoRequest): Async<GeoResponse> {

        with(myClient) {
            launch {
                val call =  call(myHost) {
                    method = HttpMethod.Post
                    header("Content-Type", "application/json")
                    body = format(request)
                }

            }
        }

        return ThreadSafeAsync()
    }
}
