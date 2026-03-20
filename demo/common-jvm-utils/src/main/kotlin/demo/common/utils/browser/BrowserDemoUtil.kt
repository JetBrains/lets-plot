/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.common.utils.browser

import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import java.awt.Desktop
import java.io.File
import java.io.FileWriter
import java.io.StringWriter
import kotlin.io.path.Path
import kotlin.io.path.name
import kotlin.io.path.pathString

object BrowserDemoUtil {
    private const val ROOT_ELEMENT_ID = "root"

    private const val PROD_JS_OUTPUT_DIR = "build/kotlin-webpack/js/productionExecutable"
    private const val DEV_JS_OUTPUT_DIR = "build/kotlin-webpack/js/developmentExecutable"

    private const val PROD_LETS_PLOT_PATH = "js-package/$PROD_JS_OUTPUT_DIR/lets-plot.js"
    private const val DEV_LETS_PLOT_PATH = "js-package/$DEV_JS_OUTPUT_DIR/lets-plot.js"

    fun openInBrowser(demoProjectRelativePath: String, html: () -> String) {
        val file = createDemoFile(
            demoProjectRelativePath,
            "index", "html"
        )

        val content = html()
        FileWriter(file).use {
            it.write(content)
        }

        val desktop = Desktop.getDesktop()
        desktop.browse(file.toURI())
    }

    fun createDemoFile(
        demoProjectRelativePath: String,
        filenamePrefix: String,
        filenameExtension: String
    ): File {
        val rootPath = getRepoRootPath()
        println("Project root: $rootPath")
        println("demo relative path: $demoProjectRelativePath")
        val tmpDir = File(rootPath, "$demoProjectRelativePath/build/tmp")
        tmpDir.mkdirs()
        val file = File.createTempFile(filenamePrefix, ".$filenameExtension", tmpDir)
        println(file.canonicalFile)
        return file
    }

    fun isDev(dev: Boolean? = null): Boolean = dev ?: (System.getenv()["DEV"] != null)

    fun getRepoRootPath(): String {
        fun isRepoRootPath(path: String): Boolean {
            val fileList = Path(path).toFile().listFiles()?.map { file -> file.canonicalFile.toPath() } ?: return false

            // Check core demo dirs - "demo" and "js-package"
            if (!fileList.any { it.name == "demo" }) return false
            if (!fileList.any { it.name == "js-package" }) return false

            return true
        }

        println("=== Determining project root path ===")

        // First, try to get a PWD env variable - might be set by running configurations in IDEA
        // On linux PWD points to the user home dir
        val pwdPath = System.getenv()["PWD"]
        println("System.getenv()[\"PWD\"]: $pwdPath")

        if (pwdPath != null && isRepoRootPath(pwdPath)) {
            return pwdPath
        }

        // Fallback to traversal logic
        var curDir: String? = System.getProperty("user.dir")
        println("System.getProperty('user.dir'): $curDir")

        while (curDir != null) {
            println("Checking directory: $curDir")
            if (isRepoRootPath(curDir)) {
                println("✓ Found repo root via traversal: $curDir")
                return curDir
            } else {
                curDir = Path(curDir).parent.pathString
            }
        }

        throw IllegalStateException("Could not determine project root. PWD: $pwdPath, user.dir: ${System.getProperty("user.dir")}")
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

    private fun projectJs(projectPath: String, projectName: String, dev: Boolean? = null): String {
        return "${getRepoRootPath()}/$projectPath/${getJsOutputDir(dev)}/$projectName.js"
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
        val mainScript = projectJs(demoProjectPath, demoProject, dev)
        val writer = StringWriter().appendHTML().html {
            lang = "en"
            head {
                title(title)
            }
            body {

                script {
                    type = "text/javascript"
                    src = getPlotLibPath(dev)
                }

                if (projectDeps != null) {
                    for (projectDep in projectDeps) {
                        script {
                            type = "text/javascript"
                            src = projectJs("to-do", projectDep)           // ToDo
                        }
                    }
                }

                script {
                    type = "text/javascript"
                    src = mainScript
                }

                div { id = ROOT_ELEMENT_ID }

                script {
                    type = "text/javascript"
                    unsafe {
                        +"""
                        |
                        |   window['$demoProject'].$callFun();
                    """.trimMargin()

                    }
                }
            }
        }

        return writer.toString()
    }

}
