/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.browser.plotSvg

import demo.common.utils.browser.BrowserDemoUtil
import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import org.jetbrains.letsPlot.awt.plot.PlotSvgExport
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import java.io.StringWriter

object PlotSvgDemoUtil {
    private const val DEMO_PROJECT_PATH = "demo/plot-browser"

    fun show(
        title: String,
        plotSpecList: List<MutableMap<String, Any>>,
        plotSize: DoubleVector = DoubleVector(400.0, 300.0)
    ) {
        BrowserDemoUtil.openInBrowser(DEMO_PROJECT_PATH) {
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
                                background-color:lightgrey
                            }
                        """.trimIndent()
                    }
                }
            }
            body {
                for (plotSpec in plotSpecList) {
                    val svgImage = PlotSvgExport.buildSvgImageFromRawSpecs(plotSpec, plotSize)
                    div("demo") {
                        unsafe { +svgImage }
                    }
                }
            }
        }

        return writer.toString()
    }
}