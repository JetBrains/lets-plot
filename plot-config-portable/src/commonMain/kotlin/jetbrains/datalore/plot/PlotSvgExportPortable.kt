/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.logging.PortableLogging
import jetbrains.datalore.plot.PlotSvgHelper.fetchPlotSizeFromSvg
import jetbrains.datalore.plot.config.BunchConfig
import jetbrains.datalore.plot.config.FigKind
import jetbrains.datalore.plot.config.PlotConfig
import jetbrains.datalore.vis.svgToString.SvgToString

object PlotSvgExportPortable {
    private val LOG = PortableLogging.logger(PlotSvgExportPortable::class)
    private val PORTABLE_SVG_STR_MAPPER =
        SvgToString(rgbEncoder = null)   // data-frame --> rgb image is not supported (geom_raster)

    /**
     * @param plotSpec Raw specification of a plot or GGBunch.
     * @param useCssPixelatedImageRendering true for CSS style "pixelated", false for SVG style "optimizeSpeed". Used for compatibility.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun buildSvgImageFromRawSpecs(
        plotSpec: MutableMap<String, Any>,
        useCssPixelatedImageRendering: Boolean
    ): String {
        return buildSvgImageFromRawSpecs(plotSpec, plotSize = null, SvgToString(rgbEncoder = null, useCssPixelatedImageRendering))
    }

    /**
     * @param plotSpec Raw specification of a plot or GGBunch.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun buildSvgImageFromRawSpecs(
        plotSpec: MutableMap<String, Any>,
        svgToString: SvgToString = PORTABLE_SVG_STR_MAPPER
    ): String {
        return buildSvgImageFromRawSpecs(plotSpec, null, svgToString)
    }

    /**
     * @param plotSpec Raw specification of a plot or GGBunch.
     * @param plotSize Desired plot size. Has no effect on GGBunch.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun buildSvgImageFromRawSpecs(
        plotSpec: MutableMap<String, Any>,
        plotSize: DoubleVector?,
        svgToString: SvgToString = PORTABLE_SVG_STR_MAPPER
    ): String {
        val list = MonolithicCommon.buildSvgImagesFromRawSpecs(
            plotSpec,
            plotSize,
            svgToString
        ) { messages ->
            messages.forEach {
                LOG.info { "[when SVG generating] $it" }
            }
        }

        if (list.isEmpty()) {
            throw IllegalStateException("Nothing to save: the plot is empty.")
        }

        if (list.size == 1) {
            return list[0]
        }

        // Must be GGBunch
        if (PlotConfig.figSpecKind(plotSpec) != FigKind.GG_BUNCH_SPEC) {
            throw IllegalStateException("Can't save multiple SVG images in one file.") // Should never happen
        }

        val bunchItemSvgList = ArrayList<String>()
        var bunchBounds = DoubleRectangle(DoubleVector.ZERO, DoubleVector.ZERO)
        val bunchItems = BunchConfig(plotSpec).bunchItems
        for ((plotSvg, bunchItem) in list.zip(bunchItems)) {
            val (itemSvg, size) = transformBunchItemSvg(
                plotSvg, bunchItem.x, bunchItem.y
            )
            bunchItemSvgList.add(itemSvg)
            bunchBounds = bunchBounds.union(DoubleRectangle(bunchItem.x, bunchItem.y, size.x, size.y))
        }

        val svgStyle = """ 
            |<style type="text/css">
            |${list.joinToString(separator = "\n", transform = ::getBunchItemSvgStyle)}
            |</style>
        """.trimMargin()
        return """<svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" class="plt-container" width="${bunchBounds.width}" height="${bunchBounds.height}">
            |$svgStyle
            |${bunchItemSvgList.joinToString(separator = "\n")}
            |</svg>
        """.trimMargin()
    }

    private fun getBunchItemSvgStyle(svg: String): String {
        val split = svg.split("<style type=\"text/css\">")
        val styleAtTheTop = split[1]
        return styleAtTheTop.split("</style>")[0]
    }

    @Suppress("MemberVisibilityCanBePrivate")
    private fun transformBunchItemSvg(
        svg: String,
        x: Double,
        y: Double
    ): Pair<String, DoubleVector> {
        val split = svg.split("</style>")
        val rootGroup = split[1].split("</svg>")[0]

        val rootGroupTranslated =
            """<g transform="translate($x $y)" ${
                rootGroup.substring(
                    rootGroup.indexOf("<g ") + 3
                )
            }"""

        val svgSize = fetchPlotSizeFromSvg(svg)
        return Pair(rootGroupTranslated, svgSize)
    }
}