/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.demo

import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import java.awt.Desktop
import java.io.File
import java.io.FileWriter
import java.io.StringWriter

object BrowserDemoUtil {
    val KOTLIN_LIBS = listOf(
        "kotlin.js",
        "kotlin-logging.js",
        "kotlin-test.js",
        "kotlinx-io.js",
        "kotlinx-coroutines-core.js",
        "kotlinx-coroutines-io.js",
        "ktor-ktor-utils.js",
        "ktor-ktor-http.js",
        "ktor-ktor-http-cio.js",
        "ktor-ktor-client-core.js"
    )

    val BASE_MAPPER_LIBS = listOf(
//        "base.js",
        "datalore-plot-base-portable.js",
        "datalore-plot-base.js",
        "mapper-core.js",
        "vis-svg.js",
        "vis-canvas.js"
    )

    val PLOT_LIBS = listOf(
        "base-canvas.js",     // required by plot-builder (get rid?)
        "plot-common-portable.js",
        "plot-common.js",
        "plot-base-portable.js",
        "plot-base.js",
        "plot-builder-portable.js",
        "plot-builder.js",
        "plot-config-portable.js",
        "gis.js",
        "livemap.js"
    )

    val DEMO_COMMON_LIBS = listOf(
        "package jetbrains.datalore.vis.js"
    )

    private const val ROOT_PROJECT = "lets-plot"

    fun openInBrowser(demoProject: String, html: () -> String) {
        val outputDir = "$demoProject/build/demoWeb"

        val projectRoot = getProjectRoot()
        println("Project root: $projectRoot")
        val tmpDir = File(projectRoot, outputDir)
        val file = File.createTempFile("index", ".html", tmpDir)
        println(file.canonicalFile)

        FileWriter(file).use {
            it.write(html())
        }

        val desktop = Desktop.getDesktop()
        desktop.browse(file.toURI());
    }

    private fun getProjectRoot(): String {
        // works when launching from IDEA
        val projectRoot = System.getenv()["PWD"] ?: error("'PWD' env variable is not defined")

        if (!projectRoot.contains(ROOT_PROJECT)) {
            throw IllegalStateException("'PWD' is not pointing to $ROOT_PROJECT : $projectRoot")
        }
        return projectRoot
    }


    fun mapperDemoHtml(demoProject: String, callFun: String, libs: List<String>, title: String): String {
        val mainScript = "$demoProject.js"
        val writer = StringWriter().appendHTML().html {
            lang = "en"
            head {
                title(title)
            }
            body {
                div { id = "root" }

                for (lib in libs) {
                    script {
                        type = "text/javascript"
                        src = "lib/$lib"
                    }
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