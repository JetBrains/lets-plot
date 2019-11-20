/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.demoUtils.browser

import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import java.awt.Desktop
import java.io.File
import java.io.FileWriter
import java.io.StringWriter

object BrowserDemoUtil {
    val KOTLIN_LIBS = listOf(
        "kotlin.js",
        "kotlin-logging.js"
    )

    val BASE_MAPPER_LIBS = listOf(
        "datalore-plot-base-portable.js",          // base-portable
        "datalore-plot-base.js",                   // base
        "mapper-core.js",
        "vis-svg.js",
        "vis-svg-mapper.js"
    )

    val PLOT_LIBS = listOf(
        "vis-canvas.js",     // required by plot-builder (get rid?)
        "plot-common-portable.js",
        "plot-common.js",
        "plot-base-portable.js",
        "plot-base.js",
        "plot-builder-portable.js",
        "plot-builder.js",
        "kotlinx-io.js",
        "kotlinx-coroutines-core.js",
        "kotlinx-coroutines-io.js",
        "ktor-ktor-utils.js",
        "ktor-ktor-http.js",
        "ktor-ktor-http-cio.js",
        "ktor-ktor-client-core.js",
        "gis.js",
        "livemap.js",
        "plot-config-portable.js",
        "livemap-geom.js",
        "plot-config.js"
    )

    val DEMO_COMMON_LIBS = listOf(
        "vis-demo-common.js"
    )

    private const val ROOT_PROJECT = "datalore-plot"
    private const val JS_PATH = "js-package/build/js"
    private const val ROOT_ELEMENT_ID = "root"

    fun openInBrowser(demoProjectRelativePath: String, html: () -> String) {

        val rootPath = getRootPath()
        println("Project root: $rootPath")
        val tmpDir = File(rootPath, "$demoProjectRelativePath/build/tmp")
        val file = File.createTempFile("index", ".html", tmpDir)
        println(file.canonicalFile)

        FileWriter(file).use {
            it.write(html())
        }

        val desktop = Desktop.getDesktop()
        desktop.browse(file.toURI())
    }

    fun getRootPath(): String {
        // works when launching from IDEA
        val projectRoot = System.getenv()["PWD"] ?: throw IllegalStateException("'PWD' env variable is not defined")

        if (!projectRoot.contains(ROOT_PROJECT)) {
            throw IllegalStateException("'PWD' is not pointing to $ROOT_PROJECT : $projectRoot")
        }
        return projectRoot
    }

    fun mapperDemoHtml(demoProject: String, callFun: String, libs: List<String>, title: String): String {
        val mainScript = "${getRootPath()}/$demoProject/build/classes/kotlin/js/main/$demoProject.js"
        val writer = StringWriter().appendHTML().html {
            lang = "en"
            head {
                title(title)
            }
            body {
                for (lib in libs) {
                    script {
                        type = "text/javascript"
                        src = "${getRootPath()}/$JS_PATH/$lib"
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