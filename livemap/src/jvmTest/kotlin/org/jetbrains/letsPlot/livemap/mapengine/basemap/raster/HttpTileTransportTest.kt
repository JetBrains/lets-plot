/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.mapengine.basemap.raster

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.nio.charset.StandardCharsets
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import kotlin.test.*
import kotlin.time.Duration.Companion.milliseconds

class HttpTileTransportTest {
    @Test
    fun responseExceptionIsReportedAsStatusFailure() {
        withTestServer { server ->
            val failureRef = AtomicReference<Throwable?>()
            val completed = CountDownLatch(1)

            HttpTileTransport().get(server.url("/not-found.png")).onResult(
                successHandler = {
                    failureRef.set(AssertionError("Expected failure, got success"))
                    completed.countDown()
                },
                failureHandler = {
                    failureRef.set(it)
                    completed.countDown()
                }
            )

            assertTrue(completed.await(2, TimeUnit.SECONDS), "Async result was not completed")
            assertEquals("404 Not Found", failureRef.get()?.message)
        }
    }

    @Test
    fun hangingResponseBodyTimesOutAndFails() {
        withTestServer { server ->
            val failureRef = AtomicReference<Throwable?>()
            val completed = CountDownLatch(1)

            HttpTileTransport().get(server.url("/hanging-body.png"), 200.milliseconds).onResult(
                successHandler = {
                    failureRef.set(AssertionError("Expected failure, got success"))
                    completed.countDown()
                },
                failureHandler = {
                    failureRef.set(it)
                    completed.countDown()
                }
            )

            assertTrue(completed.await(2, TimeUnit.SECONDS), "Async result was not completed")
            assertIs<TimeoutCancellationException>(failureRef.get())
        }
    }

    @Test
    fun redirectToHtmlPageCompletesWithoutHanging() {
        withTestServer { server ->
            val successRef = AtomicReference<ByteArray?>()
            val failureRef = AtomicReference<Throwable?>()
            val completed = CountDownLatch(1)

            HttpTileTransport().get(server.url("/redirect-to-html.png")).onResult(
                successHandler = {
                    successRef.set(it)
                    completed.countDown()
                },
                failureHandler = {
                    failureRef.set(it)
                    completed.countDown()
                }
            )

            assertTrue(completed.await(2, TimeUnit.SECONDS), "Async result was not completed")
            if (failureRef.get() != null) {
                fail("Expected redirect request to complete successfully, but got: ${failureRef.get()}")
            }
            assertEquals("<html><body>redirect target</body></html>", successRef.get()!!.toString(StandardCharsets.UTF_8))
        }
    }

    private fun withTestServer(block: (TestServer) -> Unit) {
        val server = TestServer().also { it.start() }
        try {
            block(server)
        } finally {
            server.stop()
        }
    }

    private class TestServer {
        private var server: EmbeddedServer<*, *>? = null
        private var port: Int = -1

        fun start() {
            val serverInstance = embeddedServer(CIO, port = 0) {
                testRouting()
            }
            serverInstance.start(wait = false)

            server = serverInstance
            port = runBlocking {
                serverInstance.engine.resolvedConnectors().first().port
            }
        }

        fun stop() {
            server?.stop(0, 0)
            server = null
        }

        fun url(path: String): String {
            return "http://localhost:$port$path"
        }
    }
}

private fun Application.testRouting() {
    routing {
        get("/not-found.png") {
            call.respond(HttpStatusCode.NotFound, "Tile not found")
        }

        get("/hanging-body.png") {
            call.respondTextWriter(ContentType.Text.Plain) {
                write("partial")
                flush()
                delay(5_000)
            }
        }

        get("/redirect-to-html.png") {
            call.respondRedirect("/tiles.html", permanent = false)
        }

        get("/tiles.html") {
            call.respondTextWriter(ContentType.Text.Html) {
                write("<html><body>redirect target</body></html>")
            }
        }
    }
}
