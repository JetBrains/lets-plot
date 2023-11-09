/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.livemap.demo

import demo.common.util.demoUtils.browser.BrowserDemoUtil
import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import java.awt.Desktop
import java.io.File
import java.io.FileWriter
import java.io.StringWriter

object BrowserDemoUtil {

    private const val ROOT_PROJECT = "lets-plot"
    private const val DEMO_PROJECT_PATH = "demo/livemap"

    private fun getPlotLibPath(dev: Boolean): String {
        val name = "lets-plot.min.js"
        val dist = when {
            dev -> "js-package/build/dist/js/developmentExecutable"
            else -> "js-package/build/dist/js/productionExecutable"
        }
        return "${BrowserDemoUtil.getRootPath()}/$dist/$name"
    }

    private fun getDemoDir(dev: Boolean = false) = when {
        dev -> "build/dist/js/developmentExecutable"
        else -> "build/dist/js/productionExecutable"
    }

    fun openInBrowser(dev: Boolean = false, html: () -> String) {
        val outputDir = "$DEMO_PROJECT_PATH/${getDemoDir(dev)}"

        val projectRoot = getProjectRoot()
        println("Project root: $projectRoot")
        val tmpDir = File(projectRoot, outputDir)
        val file = File.createTempFile("index", ".html", tmpDir)
        println(file.canonicalFile)

        FileWriter(file).use {
            it.write(html())
        }

        val desktop = Desktop.getDesktop()
        desktop.browse(file.toURI())
    }

    private fun getProjectRoot(): String {
        // works when launching from IDEA
        val projectRoot = System.getenv()["PWD"] ?: error("'PWD' env variable is not defined")

        if (!projectRoot.contains(ROOT_PROJECT)) {
            throw IllegalStateException("'PWD' is not pointing to $ROOT_PROJECT : $projectRoot")
        }
        return projectRoot
    }

    fun mapperDemoHtml(demoProject: String, callFun: String, title: String, dev: Boolean = false): String {
        val mainScript = "$demoProject.js"
        val writer = StringWriter().appendHTML().html {
            lang = "en"
            head {
                title(title)
            }
            body {
                div { id = "root" }

                script {
                    type = "text/javascript"
                    src = getPlotLibPath(dev)
                }

                script {
                    type = "text/javascript"
                    src = mainScript
                }

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
