/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.jsObject.JsObjectSupport
import jetbrains.datalore.base.logging.PortableLogging
import jetbrains.datalore.base.random.RandomString
import jetbrains.datalore.plot.config.FigKind
import jetbrains.datalore.plot.config.PlotConfig
import jetbrains.datalore.plot.config.PlotConfigClientSide
import jetbrains.datalore.plot.config.PlotConfigUtil
import jetbrains.datalore.plot.server.config.BackendSpecTransformUtil
import kotlin.math.round

object PlotHtmlExport {
    private val LOG = PortableLogging.logger(PlotHtmlExport::class)

    /**
     * @param plotSpec Raw specification of a plot or GGBunch.
     * @param scriptUrl An URL to load the Lets-plot JS library from.
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

        val configureHtml = getStaticConfigureHtml(scriptUrl)
        val displayHtml = getStaticDisplayHtmlForRawSpec(plotSpec, plotSize)

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

            return when (val kind = PlotConfig.figSpecKind(plotSpec)) {
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

    private fun getStaticConfigureHtml(scriptUrl: String): String {
        return "<script type=\"text/javascript\" ${PlotHtmlHelper.ATT_SCRIPT_KIND}=\"${PlotHtmlHelper.SCRIPT_KIND_LIB_LOADING}\" src=\"$scriptUrl\"></script>"
    }

    private fun getStaticDisplayHtmlForRawSpec(plotSpec: MutableMap<String, Any>, size: DoubleVector? = null): String {
        // server-side transforms: statistics, sampling, etc.
        @Suppress("NAME_SHADOWING")
        val plotSpec = BackendSpecTransformUtil.processTransform(plotSpec)

        PlotConfigUtil.findComputationMessages(plotSpec).forEach { LOG.info { "[when HTML generating] $it" } }

        // Remove computation messages from the output
        PlotConfigUtil.removeComputationMessages(plotSpec)

        val plotSpecJs = JsObjectSupport.mapToJsObjectInitializer(plotSpec)
        return getStaticDisplayHtml(plotSpecJs, size)
    }

    private fun getStaticDisplayHtml(
        plotSpecAsJsObjectInitializer: String,
        size: DoubleVector?
    ): String {
        val outputId = RandomString.randomString(6)
        val dim = if (size == null) "-1, -1" else "${size.x}, ${size.y}"
        return """
            |   <div id="$outputId"></div>
            |   <script type="text/javascript" ${PlotHtmlHelper.ATT_SCRIPT_KIND}="${PlotHtmlHelper.SCRIPT_KIND_PLOT}">
            |       var plotSpec=$plotSpecAsJsObjectInitializer;
            |       var plotContainer = document.getElementById("$outputId");
            |       LetsPlot.buildPlotFromProcessedSpecs(plotSpec, ${dim}, plotContainer);
            |   </script>
        """.trimMargin()
    }
}