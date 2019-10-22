package jetbrains.gis.tileprotocol.http

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.http.DEFAULT_PORT
import io.ktor.http.URLProtocol
import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.async.ThreadSafeAsync
import kotlinx.coroutines.launch

class TileTransport(private val myHost: String, private val myPort: Int?, private val mySubUrl: String) {
    private val myClient = HttpClient()

    fun get(tileString: String): Async<ByteArray> {
        val async = ThreadSafeAsync<ByteArray>()

        myClient.launch {

            val response= myClient.get<ByteArray> {
                url {
                    protocol = URLProtocol.HTTPS
                    host = myHost
                    port = myPort ?: DEFAULT_PORT
                    encodedPath = mySubUrl + tileString
                }
            }

            async.success(response)
        }

        return async
    }
}