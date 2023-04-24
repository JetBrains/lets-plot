/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.config.FigKind
import jetbrains.datalore.plot.config.PlotConfig
import jetbrains.datalore.plot.config.PlotConfigClientSide
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
        val displayHtml = PlotHtmlHelper.getStaticDisplayHtmlForRawSpec(plotSpec, plotSize, removeComputationMessages = true, logComputationMessages = true)

        val style = if (iFrame) {
            "\n       <style> html, body { margin: 0; overflow: hidden; } </style>"
        } else {
            ""
        }
        val html = """
            |<html lang="en">
            |   <head>$style
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
                    val config = PlotConfigClientSide.create(plotSpec) { /*ignore messages*/ }
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