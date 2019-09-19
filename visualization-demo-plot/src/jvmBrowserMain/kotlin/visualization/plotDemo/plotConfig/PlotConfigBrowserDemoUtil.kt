package jetbrains.datalore.visualization.plotDemo.plotConfig

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.jsObject.mapToJsObjectInitializer
import jetbrains.datalore.visualization.demoUtils.browser.BrowserDemoUtil
import jetbrains.datalore.visualization.demoUtils.browser.BrowserDemoUtil.BASE_MAPPER_LIBS
import jetbrains.datalore.visualization.demoUtils.browser.BrowserDemoUtil.DEMO_COMMON_LIBS
import jetbrains.datalore.visualization.demoUtils.browser.BrowserDemoUtil.KOTLIN_LIBS
import jetbrains.datalore.visualization.demoUtils.browser.BrowserDemoUtil.PLOT_LIBS
import jetbrains.datalore.visualization.plot.server.config.PlotConfigServerSide
import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import java.io.StringWriter

private const val DEMO_PROJECT = "visualization-demo-plot"
private const val CALL_MODULE = "visualization-plot-config"
private const val CALL_FUN = "jetbrains.datalore.visualization.plot.MonolithicJs.buildPlotFromProcessedSpecs"
private val LIBS = KOTLIN_LIBS + BASE_MAPPER_LIBS + PLOT_LIBS + DEMO_COMMON_LIBS

object PlotConfigDemoUtil {
    fun show(title: String, plotSpecList: List<MutableMap<String, Any>>, plotSize: DoubleVector) {
        BrowserDemoUtil.openInBrowser(DEMO_PROJECT) {
            getHtml(
                title,
                plotSpecList,
                plotSize
            )
        }
    }

    private fun getHtml(
        title: String,
        plotSpecList: List<MutableMap<String, Any>>,
        plotSize: DoubleVector
    ): String {

        val plotSpecListJs = StringBuilder("[\n")
        @Suppress("UNCHECKED_CAST")
        var first = true
        for (spec in plotSpecList) {
            @Suppress("NAME_SHADOWING")
            val spec = PlotConfigServerSide.processTransform(spec)
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

                for (lib in LIBS) {
                    script {
                        type = "text/javascript"
                        src = "lib/$lib"
                    }
                }

                script {
                    type = "text/javascript"
                    unsafe {
                        +"""
                        |var plotSpecList=$plotSpecListJs;
                        |plotSpecList.forEach(function (spec, index) {
                        |
                        |   var parentElement = document.createElement('div');
                        |   document.getElementById("root").appendChild(parentElement);
                        |   window['$CALL_MODULE'].$CALL_FUN(spec, ${plotSize.x}, ${plotSize.y}, parentElement);
                        |   
                        |});
                    """.trimMargin()

                    }
                }
            }
        }

        return writer.toString()
    }
}