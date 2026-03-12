/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.common.utils.browser

import com.sun.net.httpserver.HttpServer
import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import java.awt.Desktop
import java.io.File
import java.io.FileWriter
import java.io.StringWriter
import java.net.InetSocketAddress
import java.net.URI
import java.nio.file.Path
import kotlin.io.path.Path

object WasmJsDemoUtil {
    private const val ROOT_DEMO_DIR_NAME = "demo"
    private const val ROOT_PROJECT = "lets-plot"
    private const val ROOT_ELEMENT_ID = "root"

    private const val PROD_WASM_OUTPUT_DIR = "build/dist/wasmJs/productionExecutable"
    private const val DEV_WASM_OUTPUT_DIR = "build/kotlin-webpack/wasmJs/developmentExecutable"

    private const val PROD_LETS_PLOT_PATH = "js-package/$PROD_WASM_OUTPUT_DIR/lets-plot.mjs"
    private const val DEV_LETS_PLOT_PATH = "js-package/$DEV_WASM_OUTPUT_DIR/lets-plot.mjs"

    private var server: HttpServer? = null
    private var serverPort: Int = 0

    fun openInBrowser(demoProjectRelativePath: String, html: () -> String) {
        val file = createDemoFile(
            demoProjectRelativePath,
            "index", "html"
        )

        val content = html()
        FileWriter(file).use {
            it.write(content)
        }

        // ES modules and Wasm require an HTTP server due to CORS restrictions on file:// URIs.
        startServerAndBrowse(file)
    }

    private fun startServerAndBrowse(indexFile: File) {
        if (server == null) {
            val rootPath = getRootPath()
            server = HttpServer.create(InetSocketAddress("localhost", 0), 0).apply {
                createContext("/") { exchange ->
                    try {
                        val path = exchange.requestURI.path
                        val fileToServe = if (path == "/" || path == "/index.html") {
                            indexFile
                        } else {
                            File(rootPath, path)
                        }

                        if (fileToServe.exists()) {
                            val bytes = fileToServe.readBytes()
                            val mimeType = when (fileToServe.extension.lowercase()) {
                                "html" -> "text/html"
                                "js", "mjs" -> "application/javascript"
                                "wasm" -> "application/wasm"
                                "css" -> "text/css"
                                else -> "text/plain"
                            }
                            exchange.responseHeaders.add("Content-Type", mimeType)
                            // Allow Cross-Origin just in case modules invoke each other
                            exchange.responseHeaders.add("Access-Control-Allow-Origin", "*")
                            exchange.sendResponseHeaders(200, bytes.size.toLong())
                            exchange.responseBody.use { it.write(bytes) }
                        } else {
                            exchange.sendResponseHeaders(404, 0)
                            exchange.responseBody.close()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        exchange.sendResponseHeaders(500, 0)
                        exchange.responseBody.close()
                    }
                }
                start()
            }
            serverPort = server!!.address.port
            println("Local dev server started for Wasm testing on http://localhost:$serverPort")
        }

        val desktop = Desktop.getDesktop()
        desktop.browse(URI("http://localhost:$serverPort/index.html"))
    }

    fun createDemoFile(
        demoProjectRelativePath: String,
        filenamePrefix: String,
        filenameExtension: String
    ): File {
        val rootPath = getRootPath()
        println("Project root: $rootPath")
        println("demo relative path: $demoProjectRelativePath")
        val tmpDir = File(rootPath, "$demoProjectRelativePath/build/tmp")
        tmpDir.mkdirs() // Make sure directory exists
        val file = File.createTempFile(filenamePrefix, ".$filenameExtension", tmpDir)
        println(file.canonicalFile)
        return file
    }

    fun isDev(dev: Boolean? = null): Boolean = dev ?: (System.getenv()["DEV"] != null)

    fun getRootPath(): String {
        println("=== Determining project root path ===")
        val pwdPath = System.getenv()["PWD"]
        println("System.getenv()[\"PWD\"]: $pwdPath")

        val workingDir = if (pwdPath != null) {
            pwdPath
        } else {
            val userDir = System.getProperty("user.dir")
            println("System.getProperty('user.dir'): $userDir")
            userDir
        }

        if (workingDir.contains(ROOT_PROJECT)) {
            println("✓ Project root contains '$ROOT_PROJECT'")
            return workingDir
        }

        println("⚠ Project root doesn't contain '$ROOT_PROJECT', trying path traversal...")

        val userDir = Path(workingDir)
        var curDir: Path? = userDir
        while (curDir != null) {
            println("Checking directory: $curDir")
            if (curDir.endsWith(ROOT_DEMO_DIR_NAME)) {
                val foundRoot = curDir.parent.toString()
                println("✓ Found project root via traversal: $foundRoot")
                return foundRoot
            } else {
                curDir = curDir.parent
            }
        }

        throw IllegalStateException("Could not determine project root. PWD: $pwdPath, user.dir: ${System.getProperty("user.dir")}")
    }

    fun getPlotLibUrl(dev: Boolean? = null): String {
        val letsPlotPath = when (isDev(dev)) {
            true -> DEV_LETS_PLOT_PATH
            false -> PROD_LETS_PLOT_PATH
        }

        val absPath = getRootPath() + "/" + letsPlotPath
        require(File(absPath).exists()) {
            if (isDev(dev))
                "Did you forget to run 'wasmJsBrowserDevelopmentWebpack'? File not found: '$absPath'"
            else
                "File not found: '$absPath'"
        }

        return "/$letsPlotPath"
    }

    fun getWasmOutputDir(dev: Boolean? = null): String {
        return when (isDev(dev)) {
            true -> DEV_WASM_OUTPUT_DIR
            false -> PROD_WASM_OUTPUT_DIR
        }
    }

    private fun projectWasmUrl(projectPath: String, projectName: String, dev: Boolean? = null): String {
        return "/$projectPath/${getWasmOutputDir(dev)}/$projectName.mjs"
    }

    fun mapperDemoHtml(
        demoProjectPath: String,
        demoProject: String,
        callFun: String,
        title: String,
        dev: Boolean? = null
    ): String {
        return mapperDemoHtml(demoProjectPath, demoProject, callFun, null, title, dev)
    }

    fun mapperDemoHtml(
        demoProjectPath: String,
        demoProject: String,
        callFun: String,
        projectDeps: List<String>?,
        title: String,
        dev: Boolean? = null
    ): String {
        val mainScriptUrl = projectWasmUrl(demoProjectPath, demoProject, dev)
        val writer = StringWriter().appendHTML().html {
            lang = "en"
            head {
                title(title)
            }
            body {
                div { id = ROOT_ELEMENT_ID }

                script {
                    type = "module"
                    unsafe {
                        +"""
                        |import * as demo from "$mainScriptUrl";
                        |
                        |// Support dynamically exported items and top level attachments in WebAssembly bindings
                        |if (typeof demo.$callFun === 'function') {
                        |    demo.$callFun();
                        |} else if (demo.$demoProject && typeof demo.$demoProject.$callFun === 'function') {
                        |    demo.$demoProject.$callFun();
                        |} else if (window['$demoProject'] && typeof window['$demoProject'].$callFun === 'function') {
                        |    window['$demoProject'].$callFun();
                        |} else {
                        |    console.error("Cannot find function $callFun natively inside the WasmJS module exports.");
                        |}
                        """.trimMargin()
                    }
                }
            }
        }

        return writer.toString()
    }
}
