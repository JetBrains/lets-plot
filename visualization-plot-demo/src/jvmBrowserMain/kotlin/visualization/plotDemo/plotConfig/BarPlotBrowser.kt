package jetbrains.datalore.visualization.plotDemo.plotConfig

import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import java.awt.Desktop
import java.io.File
import java.io.FileWriter
import java.io.StringWriter

private const val DEMO_PROJECT = "visualization-plot-demo"
private const val OUT_DIR_JS = "$DEMO_PROJECT/build/demoWeb"

private const val MAIN_JS = "$DEMO_PROJECT.js"
private val LIBS_JS = listOf(
    "kotlin.js",
    "kotlin-logging.js",
    "kotlin-test.js",

    "base.js",
    "mapper-core.js",
    "visualization-base-svg.js",
    "visualization-base-svg-mapper.js",
    "visualization-base-canvas.js",

    "visualization-plot-common.js",
    "visualization-plot-base.js",
    "visualization-plot-builder.js",
    "visualization-plot-config.js"
)


fun main() {
    val projectRoot = getProjectRoot()
    println("Project root: $projectRoot")
    val tmpDir = File(projectRoot, OUT_DIR_JS)
    val file = File.createTempFile("index", ".html", tmpDir)
    println(file.canonicalFile)

    val html = genIndexHtml()
    FileWriter(file).use {
        it.write(html)
    }

    val desktop = Desktop.getDesktop()
    desktop.browse(file.toURI());
}

private fun getProjectRoot(): String {
    // works when launching from IDEA
    val projectRoot = System.getenv()["PWD"] ?: throw IllegalStateException("'PWD' env variable is not defined")

    val rootProjectName = "datalore-plot"
    if (!projectRoot.contains(rootProjectName)) {
        throw IllegalStateException("'PWD' is not pointing to $rootProjectName : $projectRoot")
    }

    return projectRoot
}

private fun genIndexHtml(): String {
    val writer = StringWriter().appendHTML().html {
        lang = "en"
        head {
            title("SVG - DOM mapper demo")
        }
        body {
            div { id = "root" }

            for (lib in LIBS_JS) {
                script {
                    type = "text/javascript"
                    src = "lib/$lib"
                }
            }

            script {
                type = "text/javascript"
                src = MAIN_JS
            }
        }
    }

    return writer.toString()
}


