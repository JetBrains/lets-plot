package jetbrains.datalore.visualization.plotDemo.plotConfig

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.jsObject.mapToJsObjectInitializer
import jetbrains.datalore.visualization.plot.server.config.PlotConfigServerSide.Companion.processTransformWithoutEncoding
import jetbrains.datalore.visualization.plotDemo.model.plotConfig.BarPlot
import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import java.awt.Desktop
import java.io.File
import java.io.FileWriter
import java.io.StringWriter

private const val DEMO_PROJECT = "visualization-plot-demo"
private const val OUT_DIR_JS = "$DEMO_PROJECT/build/demoWeb"

private const val MODULE_NAME_JS = DEMO_PROJECT
private const val PLOT_FUN_JS = "jetbrains.datalore.visualization.plotDemo.plotConfig.buildPlotSvg"
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
    val plotSpecListJs = StringBuilder("[\n")
    val plotSize: DoubleVector
    with(BarPlot()) {
        plotSize = demoComponentSize
        @Suppress("UNCHECKED_CAST")
        val plotSpecList = plotSpecList() as List<MutableMap<String, Any>>
        var first = true
        for (spec in plotSpecList) {
            @Suppress("NAME_SHADOWING")
            val spec = processTransformWithoutEncoding(spec)
            if (!first) plotSpecListJs.append(',') else first = false
            plotSpecListJs.append(mapToJsObjectInitializer(spec))
        }
    }
    plotSpecListJs.append("\n]")

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

            script {
                type = "text/javascript"
                unsafe {
                    +"""
                        |var plotSpecList=$plotSpecListJs;
                        |plotSpecList.forEach(function (spec, index) {
                        |   var parentElement = document.createElement('div');
                        |   document.getElementById("root").appendChild(parentElement);
                        |   window['$MODULE_NAME_JS'].$PLOT_FUN_JS(spec, parentElement, ${plotSize.x}, ${plotSize.y});
                        |});
                    """.trimMargin()

                }
            }
        }
    }

    return writer.toString()
}


