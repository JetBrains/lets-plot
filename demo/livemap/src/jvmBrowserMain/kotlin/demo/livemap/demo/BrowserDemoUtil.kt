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

    private const val DEMO_PROJECT_PATH = "demo/livemap"

    fun openInBrowser(dev: Boolean? = null, html: () -> String) {
        val outputDir = "$DEMO_PROJECT_PATH/${BrowserDemoUtil.getJsOutputDir(dev)}"

        val projectRoot = BrowserDemoUtil.getRootPath()
        println("Project root: $projectRoot")
        val tmpDir = File(projectRoot, outputDir)

        require(tmpDir.exists()) {
            if (BrowserDemoUtil.isDev(dev)) {
                "Did you forget to run 'jsBrowserDevelopmentWebpack'? File not found: '${tmpDir.canonicalFile}'"
            } else {
                "File not found: '${tmpDir.canonicalFile}'"
            }
        }
        val file = File.createTempFile("index", ".html", tmpDir)
        println(file.canonicalFile)

        FileWriter(file).use {
            it.write(html())
        }

        val desktop = Desktop.getDesktop()
        desktop.browse(file.toURI())
    }

    fun mapperDemoHtml(demoProject: String, callFun: String, title: String, dev: Boolean? = null): String {
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
                    src = BrowserDemoUtil.getPlotLibPath(dev)
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
