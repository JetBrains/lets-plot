package jetbrains.datalore.visualization.plotDemo.plotConfig

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.jsObject.mapToJsObjectInitializer
import jetbrains.datalore.visualization.plot.server.config.PlotConfigServerSide
import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import java.awt.Desktop
import java.io.File
import java.io.FileWriter
import java.io.StringWriter

private const val ROOT_PROJECT = "datalore-plot"
private const val DEMO_PROJECT = "visualization-plot-demo"
private const val OUT_DIR_JS = "$DEMO_PROJECT/build/demoWeb"

private const val MODULE_JS = DEMO_PROJECT
private const val PLOT_FUN_JS = "jetbrains.datalore.visualization.plotDemo.plotConfig.buildPlotSvg"
private const val MAIN_SCRIPT_JS = "$DEMO_PROJECT.js"
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

internal object PlotConfigDemoUtil {
    fun show(title: String, plotSpecList: List<MutableMap<String, Any>>, plotSize: DoubleVector) {
        val projectRoot = getProjectRoot()
        println("Project root: $projectRoot")
        val tmpDir = File(projectRoot, OUT_DIR_JS)
        val file = File.createTempFile("index", ".html", tmpDir)
        println(file.canonicalFile)

        val html = createHtml(title, plotSpecList, plotSize)
        FileWriter(file).use {
            it.write(html)
        }

        val desktop = Desktop.getDesktop()
        desktop.browse(file.toURI());
    }

    private fun getProjectRoot(): String {
        // works when launching from IDEA
        val projectRoot = System.getenv()["PWD"] ?: throw IllegalStateException("'PWD' env variable is not defined")

        if (!projectRoot.contains(ROOT_PROJECT)) {
            throw IllegalStateException("'PWD' is not pointing to $ROOT_PROJECT : $projectRoot")
        }
        return projectRoot
    }

    private fun createHtml(
        title: String, plotSpecList: List<MutableMap<String, Any>>, plotSize: DoubleVector
    ): String {

        val plotSpecListJs = StringBuilder("[\n")
        @Suppress("UNCHECKED_CAST")
        var first = true
        for (spec in plotSpecList) {
            @Suppress("NAME_SHADOWING")
            val spec = PlotConfigServerSide.processTransformWithoutEncoding(spec)
            if (!first) plotSpecListJs.append(',') else first = false
            plotSpecListJs.append(mapToJsObjectInitializer(spec))
        }
        plotSpecListJs.append("\n]")

        val writer = StringWriter().appendHTML().html {
            lang = "en"
            head {
                title(title)
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
                    src = MAIN_SCRIPT_JS
                }

                script {
                    type = "text/javascript"
                    unsafe {
                        +"""
                        |var plotSpecList=$plotSpecListJs;
                        |plotSpecList.forEach(function (spec, index) {
                        |   var parentElement = document.createElement('div');
                        |   document.getElementById("root").appendChild(parentElement);
                        |   window['$MODULE_JS'].$PLOT_FUN_JS(spec, ${plotSize.x}, ${plotSize.y}, parentElement);
                        |});
                    """.trimMargin()

                    }
                }
            }
        }

        return writer.toString()
    }

}