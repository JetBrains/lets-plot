/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.util

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.spec.FigKind
import org.jetbrains.letsPlot.core.spec.config.PlotConfig
import org.jetbrains.letsPlot.core.spec.front.PlotConfigFrontend
import kotlin.math.round

object PlotHtmlExport {
    /**
     * @param plotSpec Raw specification of a plot or GGBunch.
     * @param scriptUrl A URL to load the Lets-plot JS library from.
     * @param iFrame Whether to wrap HTML in IFrame
     * @param plotSize Desired plot size. Has no effect on GGBunch.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun buildHtmlFromRawSpecs(
        plotSpec: MutableMap<String, Any>,
        scriptUrl: String,
        iFrame: Boolean = false,
        plotSize: DoubleVector? = null
    ): String {

        val configureHtml = PlotHtmlHelper.getStaticConfigureHtml(scriptUrl)
        val displayHtml = PlotHtmlHelper.getStaticDisplayHtmlForRawSpec(
            plotSpec,
            plotSize,
            removeComputationMessages = true,
            logComputationMessages = true
        )

        val style = if (iFrame) {
            "\n       <style> html, body { margin: 0; overflow: hidden; } </style>"
        } else {
            ""
        }
        val html = """
            |<html lang="en">
            |   <head>
            |       <meta charset="UTF-8">$style
            |       $configureHtml
            |   </head>
            |   <body>
            |       $displayHtml
            |   </body>
            |</html>
        """.trimMargin()

        return if (iFrame) {
            val attributes = ArrayList<String>()
            attributes.add("src='about:blank'")
            attributes.add("style='border:none !important;'")
            val preferredSize = preferredPlotSizeFromRawSpec(plotSpec, plotSize)
            if (preferredSize != null) {
                attributes.add("width='${round(preferredSize.x + 0.5).toInt()}'")
                attributes.add("height='${round(preferredSize.y + 0.5).toInt()}'")
            }
            attributes.add("srcdoc=\"${escapeHtmlAttr(html)}\"")

            """
            <iframe ${attributes.joinToString(" ")}></iframe>    
            """.trimIndent()
        } else {
            html
        }
    }

    private fun escapeHtmlAttr(value: String): String {
        return value
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace("\"", "&quot;")
    }

    private fun preferredPlotSizeFromRawSpec(
        plotSpec: MutableMap<String, Any>,
        plotSize: DoubleVector?
    ): DoubleVector? {
        try {
            @Suppress("NAME_SHADOWING")
            val plotSpec = MonolithicCommon.processRawSpecs(plotSpec, frontendOnly = false)
            if (PlotConfig.isFailure(plotSpec)) {
                return null
            }

            return when (PlotConfig.figSpecKind(plotSpec)) {
                FigKind.PLOT_SPEC,
                FigKind.SUBPLOTS_SPEC -> {
                    val config = PlotConfigFrontend.create(plotSpec) { /*ignore messages*/ }
                    PlotSizeHelper.singlePlotSize(
                        plotSpec, plotSize,
                        plotMaxWidth = null,
                        plotPreferredWidth = null,
                        config.facets, config.containsLiveMap
                    )
                }

                FigKind.GG_BUNCH_SPEC -> {
                    PlotSizeHelper.plotBunchSize(plotSpec)
                }
            }
        } catch (e: RuntimeException) {
            return null
        }
    }
}