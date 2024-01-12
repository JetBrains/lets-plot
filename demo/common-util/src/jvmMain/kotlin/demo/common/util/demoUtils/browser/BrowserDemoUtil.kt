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

object BrowserDemoUtil {
    private const val ROOT_PROJECT = "lets-plot"
    private const val ROOT_ELEMENT_ID = "root"
    private const val JS_DIST_PATH = "js-package/build/dist/js/productionExecutable"

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

//    private fun openInBrowser(demoProjectRelativePath: String, filePref: String, fileSuff: String, html: () -> String) {
//
//        val rootPath = getRootPath()
//        println("Project root: $rootPath")
//        val tmpDir = File(rootPath, "$demoProjectRelativePath/build/tmp")
//        val file = File.createTempFile(filePref, fileSuff, tmpDir)
//        println(file.canonicalFile)
//
//        FileWriter(file).use {
//            it.write(html())
//        }
//
//        val desktop = Desktop.getDesktop()
//        desktop.browse(file.toURI())
//    }

    fun getRootPath(): String {
        // works when launching from IDEA
        val projectRoot = System.getenv()["PWD"] ?: throw IllegalStateException("'PWD' env variable is not defined")

        if (!projectRoot.contains(ROOT_PROJECT)) {
            throw IllegalStateException("'PWD' is not pointing to $ROOT_PROJECT : $projectRoot")
        }
        return projectRoot
    }

    private fun getPlotLibPath(): String {
        val name = "lets-plot.min.js"
        return "${getRootPath()}/$JS_DIST_PATH/$name"
    }

    private fun projectJs(projectPath: String, projectName: String) =
        "${getRootPath()}/$projectPath/build/dist/js/productionExecutable/$projectName.js"

    fun mapperDemoHtml(demoProjectPath: String, demoProject: String, callFun: String, title: String): String {
        return mapperDemoHtml(demoProjectPath, demoProject, callFun, null, title)
    }

    fun mapperDemoHtml(
        demoProjectPath: String,
        demoProject: String,
        callFun: String,
        projectDeps: List<String>?,
        title: String
    ): String {
        val mainScript = projectJs(demoProjectPath, demoProject)
        val writer = StringWriter().appendHTML().html {
            lang = "en"
            head {
                title(title)
            }
            body {

                script {
                    type = "text/javascript"
                    src = getPlotLibPath()
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
