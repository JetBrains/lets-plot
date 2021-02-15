/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.config.PlotConfig
import kotlin.math.round

object PlotHtmlExport {
    /**
     * @param plotSpec Raw specification of a plot or GGBunch.
     * @param version Version of Lets-plot JS library.
     * @param iFrame Whether to wrap HTML in IFrame
     * @param plotSize Desired plot size. Has no effect on GGBunch.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun buildHtmlFromRawSpecs(
        plotSpec: MutableMap<String, Any>,
        version: String = "latest",
        iFrame: Boolean = false,
        plotSize: DoubleVector? = null
    ): String {

        val configureHtml = PlotHtmlHelper.getStaticConfigureHtml(PlotHtmlHelper.scriptUrl(version))
        val displayHtml = PlotHtmlHelper.getStaticDisplayHtmlForRawSpec(plotSpec, plotSize)

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

            return when {
                PlotConfig.isPlotSpec(plotSpec) -> {
                    val assembler = MonolithicCommon.createPlotAssembler(plotSpec) {
                        // ignore messages
                    }
                    PlotSizeHelper.singlePlotSize(plotSpec, plotSize, null, assembler.facets, assembler.containsLiveMap)
                }
                PlotConfig.isGGBunchSpec(plotSpec) -> {
                    PlotSizeHelper.plotBunchSize(plotSpec)
                }
                else -> null
            }
        } catch (e: RuntimeException) {
            return null
        }
    }
}