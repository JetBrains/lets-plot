/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.wasmjs

import demo.common.utils.browser.BrowserDemoUtil
import demo.common.utils.browser.WasmJsDemoUtil
import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.commons.jsObject.JsObjectSupportCommon.mapToJsObjectInitializer
import org.jetbrains.letsPlot.core.util.MonolithicCommon
import java.io.StringWriter

object PlotConfigWasmJsDemoUtil {
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

    private fun getPlotLibUrl(): String {
        return WasmJsDemoUtil.getPlotLibUrl()
    }

    private fun getHtml(
        title: String,
        plotSpecList: List<MutableMap<String, Any>>,
        plotSize: DoubleVector,
        applyBackendTransform: Boolean,
        backgroundColor: String
    ): String {

        val plotSpecListJs = StringBuilder("[\n")

        var first = true
        for (spec in plotSpecList) {
            @Suppress("NAME_SHADOWING")
            val spec = if (applyBackendTransform) {
                MonolithicCommon.processRawSpecs(spec)
            } else {
                spec
            }
            if (!first) plotSpecListJs.append(',') else first = false
            plotSpecListJs.append(mapToJsObjectInitializer(spec))
        }
        plotSpecListJs.append("\n]")

        val plotFun = if (applyBackendTransform) {
            "buildPlotFromProcessedSpecs"
        } else {
            "buildPlotFromRawSpecs"
        }

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
                div("demo") { id = ROOT_ELEMENT_ID }

                script {
                    type = "module" // Wasm relies on ES Modules
                    unsafe {
                        +"""
                        |import { LetsPlot } from "${getPlotLibUrl()}";
                        |
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