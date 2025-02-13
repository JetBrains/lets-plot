/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.browser.plotConfig

import demo.common.utils.browser.BrowserDemoUtil
import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.commons.jsObject.JsObjectSupportCommon.mapToJsObjectInitializer
import org.jetbrains.letsPlot.core.spec.back.SpecTransformBackendUtil
import java.io.StringWriter

object PlotConfigBrowserDemoUtil {
    private const val DEMO_PROJECT_PATH = "demo/plot-browser"
    private const val ROOT_ELEMENT_ID = "root"

    fun show(
        title: String,
        plotSpecList: List<MutableMap<String, Any>>,
        plotSize: DoubleVector = DoubleVector(400.0, 300.0),
        applyBackendTransform: Boolean = true,
        backgroundColor: String = "lightgrey"
    ) {
        BrowserDemoUtil.openInBrowser(DEMO_PROJECT_PATH) {
            getHtml(
                title,
                plotSpecList,
                plotSize,
                applyBackendTransform,
                backgroundColor
            )
        }
    }

    private fun getPlotLibPath(): String {
        return BrowserDemoUtil.getPlotLibPath()
    }

    private fun getHtml(
        title: String,
        plotSpecList: List<MutableMap<String, Any>>,
        plotSize: DoubleVector,
        applyBackendTransform: Boolean,
        backgroundColor: String
    ): String {

        val plotFun = if (applyBackendTransform) {  // see: MonolithicJs
            "buildPlotFromRawSpecs"
        } else {
            "buildPlotFromProcessedSpecs"
        }

        val plotSpecListJs = StringBuilder("[\n")

        var first = true
        for (spec in plotSpecList) {
            @Suppress("NAME_SHADOWING")
            val spec = if (applyBackendTransform) {
                SpecTransformBackendUtil.processTransform(spec)
            } else {
                spec  // raw: JS is going to apply transform on the client side
            }
            if (!first) plotSpecListJs.append(',') else first = false
            plotSpecListJs.append(mapToJsObjectInitializer(spec))
        }
        plotSpecListJs.append("\n]")

        val writer = StringWriter().appendHTML().html {
            lang = "en"
            head {
                title(title)
                style {
                    unsafe {
                        +"""
                            div.demo {
                                border: 1px solid orange;
                                margin: 20px;
                                display: inline-block;
                            }
                            body { 
                                background-color:$backgroundColor
                            }
                        """.trimIndent()
                    }
                }
            }
            body {
                script {
                    type = "text/javascript"
                    src = getPlotLibPath()
                }

                div("demo") { id = ROOT_ELEMENT_ID }

                script {
                    type = "text/javascript"
                    unsafe {
                        +"""
                        |var plotSpecList=$plotSpecListJs;
                        |plotSpecList.forEach(function (spec, index) {
                        |
                        |   var parentElement = document.createElement('div');
                        |   document.getElementById("root").appendChild(parentElement);
                        |   const sizing = {
                        |       width: ${plotSize.x},
                        |       height: ${plotSize.y}
                        |   };
                        |   
                        |   LetsPlot.$plotFun(spec, parentElement, sizing);
                        |});
                    """.trimMargin()

                    }
                }
            }
        }

        return writer.toString()
    }
}
