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
import kotlin.io.path.Path
import kotlin.io.path.name
import kotlin.io.path.pathString

object BrowserDemoUtil {
    private const val ROOT_ELEMENT_ID = "root"


    private const val PROD_JS_OUTPUT_DIR = "build/kotlin-webpack/js/productionExecutable"
    private const val DEV_JS_OUTPUT_DIR = "build/kotlin-webpack/js/developmentExecutable"
    private const val PROD_LETS_PLOT_PATH = "js-package/$PROD_JS_OUTPUT_DIR/lets-plot.js"
    private const val DEV_LETS_PLOT_PATH = "js-package/$DEV_JS_OUTPUT_DIR/lets-plot.js"

    private var server: HttpServer? = null
    private var serverPort: Int = 0

    // Enum to cleanly separate paths for JS vs WasmJS
    enum class Target(val packageName: String, val dirName: String) {
        JS("js-package", "js"),
        WASM("wasmjs-package", "wasmJs")
    }

    fun openInBrowserJs(demoProjectRelativePath: String, dev: Boolean? = null, html: () -> String) {
        openInBrowser(demoProjectRelativePath, dev, Target.JS, html)
    }

    fun openInBrowserWasm(demoProjectRelativePath: String, dev: Boolean? = null, html: () -> String) {
        openInBrowser(demoProjectRelativePath, dev, Target.WASM, html)
    }

    fun openInBrowser(demoProjectRelativePath: String, dev: Boolean? = null, html: () -> String) {
        openInBrowserJs(demoProjectRelativePath, dev, html)
    }

    private fun openInBrowser(demoProjectRelativePath: String, dev: Boolean? = null, target: Target, html: () -> String) {
        val file = createDemoFile(demoProjectRelativePath, "index_${target.name.lowercase()}", "html")

        val content = html()
        FileWriter(file).use {
            it.write(content)
        }

        // 1. Start the HTTP server to avoid CORS/file:// issues
        startLocalServer()

        // 2. Convert the file's absolute path to a URL path relative to the repo root
        val rootUri = File(getRepoRootPath()).toURI()
        val fileUri = file.toURI()
        val relativePath = rootUri.relativize(fileUri).path

        val url = "http://localhost:$serverPort/$relativePath"
        println("Opening ${target.name} demo in browser at: $url")

        // 3. Open the localhost HTTP URL
        val desktop = Desktop.getDesktop()
        desktop.browse(URI(url))
    }

    private fun startLocalServer() {
        if (server != null) return

        val rootPath = getRepoRootPath()
        // Bind to port 0 to automatically find an open, available port
        server = HttpServer.create(InetSocketAddress("localhost", 0), 0).apply {
            createContext("/") { exchange ->
                try {
                    val path = exchange.requestURI.path
                    val file = File(rootPath, path)

                    if (file.exists() && file.isFile) {
                        val bytes = file.readBytes()

                        val mimeType = when (file.extension.lowercase()) {
                            "html" -> "text/html"
                            "js", "mjs" -> "application/javascript"
                            "wasm" -> "application/wasm"
                            "css" -> "text/css"
                            else -> "text/plain"
                        }

                        exchange.responseHeaders.add("Content-Type", mimeType)
                        exchange.responseHeaders.add("Access-Control-Allow-Origin", "*")
                        exchange.sendResponseHeaders(200, bytes.size.toLong())
                        exchange.responseBody.use { it.write(bytes) }
                    } else {
                        val notFound = "404 Not Found"
                        exchange.sendResponseHeaders(404, notFound.length.toLong())
                        exchange.responseBody.use { it.write(notFound.toByteArray()) }
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
        println("Local dev server started on http://localhost:$serverPort")
    }

    fun createDemoFile(
        demoProjectRelativePath: String,
        filenamePrefix: String,
        filenameExtension: String
    ): File {
        val rootPath = getRepoRootPath()
        val tmpDir = File(rootPath, "$demoProjectRelativePath/build/tmp")
        tmpDir.mkdirs()
        return File.createTempFile(filenamePrefix, ".$filenameExtension", tmpDir)
    }

    fun isDev(dev: Boolean? = null): Boolean = dev ?: (System.getenv()["DEV"] != null)

    fun getRepoRootPath(): String {
        fun isRepoRootPath(path: String): Boolean {
            val fileList = Path(path).toFile().listFiles()?.map { file -> file.canonicalFile.toPath() } ?: return false

            // Check core demo dirs - "demo" and "js-package"
            if (!fileList.any { it.name == "demo" }) return false
            if (!fileList.any { it.name == "js-package" || it.name == "wasmjs-package" }) return false
            return true
        }

        val pwdPath = System.getenv()["PWD"]
        if (pwdPath != null && isRepoRootPath(pwdPath)) {
            return pwdPath
        }

        var curDir: String? = System.getProperty("user.dir")
        while (curDir != null) {
            if (isRepoRootPath(curDir)) {
                return curDir
            } else {
                curDir = Path(curDir).parent?.pathString
            }
        }
        throw IllegalStateException("Could not determine project root.")
    }

    private fun getPlotLibRelativePath(target: Target, dev: Boolean? = null): String {
        val modeDir = if (isDev(dev)) "developmentExecutable" else "productionExecutable"
        return "${target.packageName}/build/kotlin-webpack/${target.dirName}/$modeDir/lets-plot.js"
    }

    fun getPlotLibUrl(target: Target, dev: Boolean? = null): String {
        val relativePath = getPlotLibRelativePath(target, dev)
        val absPath = getRepoRootPath() + "/" + relativePath

        require(File(absPath).exists()) {
            val taskName = if (isDev(dev)) "${target.dirName}BrowserDevelopmentWebpack" else "${target.dirName}BrowserProductionWebpack"
            "Did you forget to run ':$taskName'? File not found: '$absPath'"
        }

        return "/$relativePath"
    }


    fun getPlotLibPath(dev: Boolean? = null): String {
        val letsPlotPath = when (isDev(dev)) {
            true -> DEV_LETS_PLOT_PATH
            false -> PROD_LETS_PLOT_PATH
        }

        val absPath = getRepoRootPath() + "/" + letsPlotPath

        require(File(absPath).exists()) {
            if (isDev(dev))
                "Did you forget to run 'jsBrowserDevelopmentWebpack'? File not found: '$absPath'"
            else
                "File not found: '$absPath'"
        }

        return absPath
    }

    fun getJsOutputDir(dev: Boolean? = null): String {
        return when (isDev(dev)) {
            true -> DEV_JS_OUTPUT_DIR
            false -> PROD_JS_OUTPUT_DIR
        }
    }

    private fun projectJsUrl(demoProjectPath: String, demoProject: String, target: Target, dev: Boolean? = null): String {
        val modeDir = if (isDev(dev)) "developmentExecutable" else "productionExecutable"
        return "/$demoProjectPath/build/kotlin-webpack/${target.dirName}/$modeDir/$demoProject.js"
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
        dev: Boolean? = null,
        target: Target = Target.JS,
    ): String {
        val mainScriptUrl = projectJsUrl(demoProjectPath, demoProject, target, dev)
        val writer = StringWriter().appendHTML().html {
            lang = "en"
            head { title(title) }
            body {
                script {
                    type = "text/javascript"
                    src = getPlotLibUrl(target, dev)
                }

                if (projectDeps != null) {
                    for (projectDep in projectDeps) {
                        script {
                            type = "text/javascript"
                            src = projectJsUrl("to-do", projectDep, target, dev)
                        }
                    }
                }

                script {
                    type = "text/javascript"
                    src = mainScriptUrl
                }

                div { id = ROOT_ELEMENT_ID }

                script {
                    type = "text/javascript"
                    unsafe { +"""window['$demoProject'].$callFun();""" }
                }
            }
        }
        return writer.toString()
    }
}