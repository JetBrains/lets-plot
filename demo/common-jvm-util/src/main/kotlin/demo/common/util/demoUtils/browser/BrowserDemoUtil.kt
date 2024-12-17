/*
 * Copyright (c) 2023. JetBrains s.r.o. 
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.common.util.demoUtils.browser

import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import java.awt.Desktop
import java.io.File
import java.io.FileWriter
import java.io.StringWriter
import java.nio.file.Path
import kotlin.io.path.Path

object BrowserDemoUtil {
    private const val ROOT_DEMO_DIR_NAME = "demo"
    private const val ROOT_PROJECT = "lets-plot"
    private const val ROOT_ELEMENT_ID = "root"

    private const val PROD_JS_OUTPUT_DIR = "build/dist/js/productionExecutable"
    private const val DEV_JS_OUTPUT_DIR = "build/dist/js/developmentExecutable"

    private const val PROD_LETS_PLOT_PATH = "js-package/$PROD_JS_OUTPUT_DIR/lets-plot.min.js"
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
        val rootPath = getRootPath()
        println("Project root: $rootPath")
        println("demo relative path: $demoProjectRelativePath")
        val tmpDir = File(rootPath, "$demoProjectRelativePath/build/tmp")
        val file = File.createTempFile(filenamePrefix, ".$filenameExtension", tmpDir)
        println(file.canonicalFile)
        return file
    }

    fun isDev(dev: Boolean? = null): Boolean = dev ?: (System.getenv()["DEV"] != null)

    fun getRootPath(): String {
        // works when launching from IDEA
        // [UPD]: on linux PWD points to user home dir
        val projectRoot = System.getenv()["PWD"] ?: throw IllegalStateException("'PWD' env variable is not defined")

        if (projectRoot.contains(ROOT_PROJECT)) {
            return projectRoot
        }

        val userDir = Path(System.getProperty("user.dir"))

        var curDir: Path? = userDir
        while (curDir != null) {
            if (curDir.endsWith(ROOT_DEMO_DIR_NAME)) {
                return curDir.parent.toString()
            } else {
                curDir = curDir.parent
            }
        }

        throw IllegalStateException("'PWD' is not pointing to $ROOT_PROJECT : $projectRoot. \nTODO: use getProperty('user.dir')")
    }

    fun getPlotLibPath(dev: Boolean? = null): String {
        val letsPlotPath = when (isDev(dev)) {
            true -> DEV_LETS_PLOT_PATH
            false -> PROD_LETS_PLOT_PATH
        }

        val absPath = getRootPath() + "/" + letsPlotPath

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
        return "${getRootPath()}/$projectPath/${getJsOutputDir(dev)}/$projectName.js"
    }

    fun mapperDemoHtml(demoProjectPath: String, demoProject: String, callFun: String, title: String, dev: Boolean? = null): String {
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
