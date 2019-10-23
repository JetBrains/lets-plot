package jetbrains.gis.tileprotocol.http

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.http.DEFAULT_PORT
import io.ktor.http.URLProtocol
import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.async.ThreadSafeAsync
import kotlinx.coroutines.launch

class HttpTileTransport(private val myHost: String, private val myPort: Int?, private val myRoute: String) {
    private val myClient = HttpClient()

    fun get(request: String): Async<ByteArray> {
        val async = ThreadSafeAsync<ByteArray>()

        myClient.launch {
            try {
                val response= myClient.get<ByteArray> {
                    url {
                        protocol = URLProtocol.HTTPS
                        host = myHost
                        port = myPort ?: DEFAULT_PORT
                        encodedPath = myRoute + request
                    }
                }

                async.success(response)
            } catch (c: Throwable) {
                async.failure(c)
            }
        }

        return async
    }
}