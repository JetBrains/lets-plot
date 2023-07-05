/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.gis.tileprotocol.http

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import org.jetbrains.letsPlot.commons.intern.async.Async
import org.jetbrains.letsPlot.commons.intern.async.ThreadSafeAsync
import kotlinx.coroutines.launch

class HttpTileTransport {
    private val myClient = HttpClient()

    fun get(url: String): Async<ByteArray> {
        val async = ThreadSafeAsync<ByteArray>()

        myClient.launch {
            try {
                val response = myClient.get(url).readBytes()
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