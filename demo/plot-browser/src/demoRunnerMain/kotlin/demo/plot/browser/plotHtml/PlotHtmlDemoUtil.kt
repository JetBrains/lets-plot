/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.browser.plotHtml

import demo.common.utils.browser.BrowserDemoUtil
import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.util.PlotHtmlExport
import java.io.StringWriter

object PlotHtmlDemoUtil {
    private const val DEMO_PROJECT_PATH = "demo/plot-browser"

    fun show(
        title: String,
        plotSpecList: List<MutableMap<String, Any>>,
        plotSize: DoubleVector? = null,
        preferredWidth: Double? = null
    ) {
        BrowserDemoUtil.openInBrowser(DEMO_PROJECT_PATH) {
            getHtml(
                title,
                plotSpecList,
                plotSize,
                preferredWidth
            )
        }
    }

    @Suppress("DuplicatedCode")
    private fun getHtml(
        title: String,
        plotSpecList: List<MutableMap<String, Any>>,
        plotSize: DoubleVector?,
        preferredWidth: Double?
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
                    // Add the preferred width data attribute if specified
                    // This simulates plot sizing mechanism in Datalore reports.
                    if (preferredWidth != null) {
                        // Create div with iframe for each plot
                        div("demo") {
                            iframe {
                                val plotHtml = PlotHtmlExport.buildHtmlFromRawSpecs(
                                    plotSpec,
                                    scriptUrl = BrowserDemoUtil.getPlotLibPath(dev = false),
                                    iFrame = false,  // Don't create iframe in the content
                                    plotSize = plotSize
                                )
                                // Insert data attribute into the body tag of the HTML content
                                val htmlWithDataAttr = plotHtml.replace(
                                    "<body>",
                                    "<body data-lets-plot-preferred-width=\"$preferredWidth\">"
                                )
                                attributes["srcdoc"] = htmlWithDataAttr
                                // Set some default styling for the iframe
//                                attributes["style"] = "border: none; width: 100%; height: 100%;"
                            }
                        }
                    } else {
                        // Original behavior with automatic iframe creation
                        div("demo") {
                            unsafe {
                                +PlotHtmlExport.buildHtmlFromRawSpecs(
                                    plotSpec,
                                    scriptUrl = BrowserDemoUtil.getPlotLibPath(dev = false),
                                    iFrame = true,
                                    plotSize = plotSize
                                )
                            }
                        }
                    }
                }
            }
        }

        return writer.toString()
    }
}