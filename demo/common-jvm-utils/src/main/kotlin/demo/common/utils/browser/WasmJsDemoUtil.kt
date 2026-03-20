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

object WasmJsDemoUtil {
    private const val ROOT_ELEMENT_ID = "root"

    private const val PROD_WASM_OUTPUT_DIR = "build/kotlin-webpack/wasmJs/productionExecutable"
    private const val DEV_WASM_OUTPUT_DIR = "build/kotlin-webpack/wasmJs/developmentExecutable"

    private const val PROD_LETS_PLOT_PATH = "wasmjs-package/$PROD_WASM_OUTPUT_DIR/lets-plot.js"
    private const val DEV_LETS_PLOT_PATH = "wasmjs-package/$DEV_WASM_OUTPUT_DIR/lets-plot.js"

    private var server: HttpServer? = null
    private var serverPort: Int = 0

    fun openInBrowser(demoProjectRelativePath: String, html: () -> String) {
        val file = BrowserDemoUtil.createDemoFile(
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
            val rootPath = BrowserDemoUtil.getRepoRootPath()
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

    fun getPlotLibUrl(dev: Boolean? = null): String {
        val letsPlotPath = when (BrowserDemoUtil.isDev(dev)) {
            true -> DEV_LETS_PLOT_PATH
            false -> PROD_LETS_PLOT_PATH
        }

        val absPath = BrowserDemoUtil.getRepoRootPath() + "/" + letsPlotPath
        require(File(absPath).exists()) {
            if (BrowserDemoUtil.isDev(dev))
                "Did you forget to run 'wasmJsBrowserDevelopmentWebpack'? File not found: '$absPath'"
            else
                "File not found: '$absPath'"
        }

        return "/$letsPlotPath"
    }

    fun getWasmOutputDir(dev: Boolean? = null): String {
        return when (BrowserDemoUtil.isDev(dev)) {
            true -> DEV_WASM_OUTPUT_DIR
            false -> PROD_WASM_OUTPUT_DIR
        }
    }

    private fun projectWasmUrl(projectPath: String, projectName: String, dev: Boolean? = null): String {
        return "/$projectPath/${getWasmOutputDir(dev)}/$projectName.js"
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
