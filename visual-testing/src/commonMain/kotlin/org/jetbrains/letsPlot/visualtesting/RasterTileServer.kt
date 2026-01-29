import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.cio.* // Server CIO
import io.ktor.server.engine.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeoutOrNull
import okio.FileSystem
import okio.Path.Companion.toPath

class RasterTileServer(
    private val tilesRoot: String,
    private val fileSystem: FileSystem
) {
    private var server: EmbeddedServer<CIOApplicationEngine, CIOApplicationEngine.Configuration>? = null

    /**
     * Tries to start the server on a free port starting from [startPort].
     * Returns the port that was successfully bound and verified.
     * Throws an exception if no server could be started after [maxRetries].
     */
    suspend fun start(startPort: Int = 8080, maxRetries: Int = 10): Int {
        val client = HttpClient(io.ktor.client.engine.cio.CIO)

        try {
            for (i in 0 until maxRetries) {
                val port = startPort + i

                try {
                    // 1. Create and Start Server
                    val candidateServer = embeddedServer(CIO, port = port) {
                        setupRouting()
                    }

                    // Attempt to start. If port is hard-locked, CIO might throw here immediately.
                    candidateServer.start(wait = false)

                    // 2. Health Check: Verify it is actually responding
                    if (isServerAlive(client, port)) {
                        this.server = candidateServer
                        return port // Success!
                    } else {
                        // Started but not responding? Stop and try next.
                        candidateServer.stop(0, 0)
                    }

                } catch (e: Exception) {
                    // Port likely in use (BindException), ignore and try next
                    // Print for debugging if needed: println("Port $port failed: ${e.message}")
                    server?.stop(0, 0)
                    server = null
                }
            }
        } finally {
            client.close()
        }

        throw IllegalStateException("Failed to find a free port between $startPort and ${startPort + maxRetries}")
    }

    /**
     * Polls the server root or status endpoint to ensure it's up.
     */
    private suspend fun isServerAlive(client: HttpClient, port: Int): Boolean {
        // Try for up to 1 second for the server to bind and respond
        return withTimeoutOrNull(1000) {
            while (true) {
                try {
                    // We hit the internal health check route
                    val response = client.get("http://localhost:$port/status-check")
                    if (response.status == HttpStatusCode.OK) {
                        return@withTimeoutOrNull true
                    }
                } catch (e: Exception) {
                    // Connection refused, wait a bit and retry
                }
                delay(50)
            }
            false
        } ?: false
    }

    fun stop() {
        server?.stop(0, 0)
        server = null
    }

    private fun Application.setupRouting() {
        routing {
            // Internal health check endpoint
            get("/status-check") {
                call.respond(HttpStatusCode.OK, "OK")
            }

            get("/{z}/{y}/{x}.png") {
                val z = call.parameters["z"]
                val y = call.parameters["y"]
                val x = call.parameters["x"]

                if (z == null || y == null || x == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }

                val path = "$tilesRoot/$z/$y/$x.png".toPath()

                if (fileSystem.exists(path)) {
                    try {
                        val bytes = fileSystem.read(path) { readByteArray() }
                        call.respondBytes(bytes, ContentType.Image.PNG)
                    } catch (e: Exception) {
                        call.respond(HttpStatusCode.InternalServerError)
                    }
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
        }
    }
}