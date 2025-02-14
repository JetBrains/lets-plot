/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.browser.plotMessage

import demo.common.utils.browser.BrowserDemoUtil
import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import org.jetbrains.letsPlot.core.util.PlotHtmlHelper
import org.jetbrains.letsPlot.core.util.sizing.SizingMode
import org.jetbrains.letsPlot.core.util.sizing.SizingPolicy
import java.io.StringWriter

object PlotMessageDemoUtil {
    private const val DEMO_PROJECT_PATH = "demo/plot-browser"

    fun show(
        title: String,
        plotSpecList: List<MutableMap<String, Any>>,
        containerWidth: Double,
    ) {
        BrowserDemoUtil.openInBrowser(DEMO_PROJECT_PATH) {
            getHtml(
                title,
                plotSpecList,
                containerWidth
            )
        }
    }

    @Suppress("DuplicatedCode")
    private fun getHtml(
        title: String,
        plotSpecList: List<MutableMap<String, Any>>,
        containerWidth: Double
    ): String {

        val scriptUrl = BrowserDemoUtil.getPlotLibPath(dev = false)
        val configureHtml = PlotHtmlHelper.getStaticConfigureHtml(scriptUrl)

        val writer = StringWriter().appendHTML().html {
            lang = "en"
            head {
                unsafe {
                    +configureHtml
                }
                title(title)
                style {
                    unsafe {
                        +"""
                            div.demo {
                                border: 1px solid orange;
                                margin: 20px;
                                width: ${containerWidth}px;
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
                    val displayHtml = PlotHtmlHelper.getDisplayHtmlForRawSpec(
                        plotSpec,
                        SizingPolicy(SizingMode.FIT, SizingMode.SCALED),
                        dynamicScriptLoading = false,
                        forceImmediateRender = false,
                        responsive = false,
                        removeComputationMessages = false,
                        logComputationMessages = false
                    )
                    div("demo") {
                        unsafe {
                            +displayHtml
                        }
                    }
                }
            }
        }

        return writer.toString()
    }
}