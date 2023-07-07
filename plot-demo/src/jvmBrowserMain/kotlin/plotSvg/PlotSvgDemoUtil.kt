/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotSvg

import jetbrains.datalore.base.geometry.DoubleVector
import org.jetbrains.letsPlot.platf.awt.plot.PlotSvgExport
import jetbrains.datalore.vis.demoUtils.browser.BrowserDemoUtil
import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import java.io.StringWriter

object PlotSvgDemoUtil {
    private const val DEMO_PROJECT = "plot-demo"

    fun show(
        title: String,
        plotSpecList: List<MutableMap<String, Any>>,
        plotSize: DoubleVector = DoubleVector(400.0, 300.0)
    ) {
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