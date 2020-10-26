/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.gis.tileprotocol.http

import io.ktor.client.HttpClient
import io.ktor.client.features.ResponseException
import io.ktor.client.request.get
import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.async.ThreadSafeAsync
import kotlinx.coroutines.launch

class HttpTileTransport {
    private val myClient = HttpClient()

    fun get(url: String): Async<ByteArray> {
        val async = ThreadSafeAsync<ByteArray>()

        myClient.launch {
            try {
                val response = myClient.get<ByteArray>(url)
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