/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.assemble

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.PlotContext
import org.jetbrains.letsPlot.core.plot.base.Scale
import org.jetbrains.letsPlot.core.plot.base.theme.Theme
import org.jetbrains.letsPlot.core.plot.builder.FrameOfReferenceProvider
import org.jetbrains.letsPlot.core.plot.builder.GeomLayer
import org.jetbrains.letsPlot.core.plot.builder.MarginalLayerUtil
import org.jetbrains.letsPlot.core.plot.builder.PlotSvgComponent
import org.jetbrains.letsPlot.core.plot.builder.coord.CoordProvider
import org.jetbrains.letsPlot.core.plot.builder.coord.PolarCoordProvider
import org.jetbrains.letsPlot.core.plot.builder.frame.BogusFrameOfReferenceProvider
import org.jetbrains.letsPlot.core.plot.builder.frame.PolarFrameOfReferenceProvider
import org.jetbrains.letsPlot.core.plot.builder.frame.SquareFrameOfReferenceProvider
import org.jetbrains.letsPlot.core.plot.builder.layout.GeomMarginsLayout
import org.jetbrains.letsPlot.core.plot.builder.layout.LegendBoxInfo
import org.jetbrains.letsPlot.core.plot.builder.layout.figure.plot.PlotFigureLayoutInfo
import org.jetbrains.letsPlot.core.plot.builder.layout.figure.plot.PlotFigureLayouter
import org.jetbrains.letsPlot.core.plot.builder.presentation.Style
import org.jetbrains.letsPlot.core.plot.builder.scale.AxisPosition
import org.jetbrains.letsPlot.datamodel.svg.style.StyleSheet

class PlotAssembler constructor(
    val geomTiles: PlotGeomTiles,
    private val facets: PlotFacets,
    private val xAxisPosition: AxisPosition,
    private val yAxisPosition: AxisPosition,
    private val theme: Theme,
    title: String? = null,
    subtitle: String? = null,
    caption: String? = null,
    private val guideOptionsMap: Map<Aes<*>, GuideOptions> = HashMap(),
) {

    val containsLiveMap: Boolean = geomTiles.containsLiveMap

    private val plotTitle = title?.takeIf { theme.plot().showTitle() }
    private val plotSubtitle = subtitle?.takeIf { theme.plot().showSubtitle() }
    private val plotCaption = caption?.takeIf { theme.plot().showCaption() }

    private var legendsEnabled = true
    private var interactionsEnabled = true

    private val frameProviderByTile: List<FrameOfReferenceProvider>

    private val plotContext: PlotContext
    private val layouter: PlotFigureLayouter

    init {
        // ToDo: use different set of scales for each tile.
        val scaleMap = geomTiles.scalesByTile()[0]
        val scaleXProto = scaleMap.getValue(Aes.X)
        val scaleYProto = scaleMap.getValue(Aes.Y)

        plotContext = PlotAssemblerPlotContext(
            geomTiles,
            scaleMap,
            theme.exponentFormat.superscript
        )

        val legendBoxInfos: List<LegendBoxInfo> = when {
            legendsEnabled -> PlotAssemblerUtil.createLegends(
                plotContext,
                geomTiles,
                geomTiles.mappersNP,
                guideOptionsMap,
                theme.legend()
            )

            else -> emptyList()
        }

        val flipAxis = geomTiles.coordProvider.flipped

        val (hAxisPosition, vAxisPosition) = when (flipAxis) {
            true -> yAxisPosition.flip() to xAxisPosition.flip()
            false -> xAxisPosition to yAxisPosition
        }

        val xyTransformedDomainsByTile: List<Pair<DoubleSpan, DoubleSpan>>? = when {
            geomTiles.containsLiveMap -> null
            else -> PositionalScalesUtil.computePlotXYTransformedDomains(
                geomTiles.coreLayersByTile(),
                scaleXProto,
                scaleYProto,
                facets
            )
        }

        frameProviderByTile = frameProviderByTile(
            coreLayersByTile = geomTiles.coreLayersByTile(),
            marginalLayersByTile = geomTiles.marginalLayersByTile(),
            coordProvider = geomTiles.coordProvider,
            scaleXProto = scaleXProto,
            scaleYProto = scaleYProto,
            rawXYTransformedDomainsByTile = xyTransformedDomainsByTile,
            containsLiveMap = geomTiles.containsLiveMap,
            hAxisPosition = hAxisPosition,
            vAxisPosition = vAxisPosition,
            theme,
            plotContext
        )

        layouter = PlotFigureLayouter(
            frameProviderByTile = frameProviderByTile,
            facets = facets,
            coordProvider = geomTiles.coordProvider,
            hAxisPosition = hAxisPosition,
            vAxisPosition = vAxisPosition,
            containsLiveMap = geomTiles.containsLiveMap,
            theme = theme,
            legendBoxInfos = legendBoxInfos,
            title = plotTitle,
            subtitle = plotSubtitle,
            caption = plotCaption
        )
    }

    fun layoutByOuterSize(size: DoubleVector): PlotFigureLayoutInfo {
        return layouter.layoutByOuterSize(size)
    }

    fun layoutByGeomSize(size: DoubleVector): PlotFigureLayoutInfo {
        return layouter.layoutByGeomSize(size)
    }

    fun createPlot(figureLayoutInfo: PlotFigureLayoutInfo): PlotSvgComponent {
        return createPlot(
            frameProviderByTile = frameProviderByTile,
            figureLayoutInfo = figureLayoutInfo,
            styleSheet = Style.fromTheme(theme, geomTiles.coordProvider.flipped),
            plotContext = plotContext
        )
    }

    private fun createPlot(
        frameProviderByTile: List<FrameOfReferenceProvider>,
        figureLayoutInfo: PlotFigureLayoutInfo,
        styleSheet: StyleSheet,
        plotContext: PlotContext
    ): PlotSvgComponent {
        return PlotSvgComponent(
            title = plotTitle,
            subtitle = plotSubtitle,
            caption = plotCaption,
            coreLayersByTile = geomTiles.coreLayersByTile(),
            marginalLayersByTile = geomTiles.marginalLayersByTile(),
            figureLayoutInfo = figureLayoutInfo,
            frameProviderByTile = frameProviderByTile,
            coordProvider = geomTiles.coordProvider,
            interactionsEnabled = interactionsEnabled,
            theme = theme,
            styleSheet = styleSheet,
            plotContext = plotContext
        )
    }

    fun disableLegends() {
        legendsEnabled = false
    }

    fun disableInteractions() {
        interactionsEnabled = false
    }

    companion object {
        fun demoAndTest(
            geomTiles: PlotGeomTiles,
            theme: Theme,
            xAxisPosition: AxisPosition = AxisPosition.BOTTOM,
            yAxisPosition: AxisPosition = AxisPosition.LEFT,
        ): PlotAssembler {

            return PlotAssembler(
                geomTiles,
                PlotFacets.UNDEFINED,
                xAxisPosition = xAxisPosition,
                yAxisPosition = yAxisPosition,
                theme = theme
            )
        }

        private fun frameProviderByTile(
            coreLayersByTile: List<List<GeomLayer>>,
            marginalLayersByTile: List<List<GeomLayer>>,
            coordProvider: CoordProvider,
            scaleXProto: Scale,
            scaleYProto: Scale,
            rawXYTransformedDomainsByTile: List<Pair<DoubleSpan, DoubleSpan>>?,
            containsLiveMap: Boolean,
            hAxisPosition: AxisPosition,
            vAxisPosition: AxisPosition,
            theme: Theme,
            plotContext: PlotContext
        ): List<FrameOfReferenceProvider> {
            if (containsLiveMap) {
                return coreLayersByTile.map { BogusFrameOfReferenceProvider() }
            }

            val domainsXYByTile = rawXYTransformedDomainsByTile!!

            val flipAxis = coordProvider.flipped
            val (hScaleProto, vScaleProto) = when (flipAxis) {
                true -> scaleYProto to scaleXProto
                else -> scaleXProto to scaleYProto
            }

            // Marginal layers.
            // Marginal layers share "marginal domain" and layout across all tiles.
            val marginalLayers = marginalLayersByTile.flatten()
            val domainByMargin = MarginalLayerUtil.marginalDomainByMargin(marginalLayers, scaleXProto, scaleYProto)
            val marginsLayout: GeomMarginsLayout = GeomMarginsLayout.create(marginalLayers)

            // Create frame of reference provider for each tile.
            return domainsXYByTile.map { (xDomain, yDomain) ->
                if (coordProvider.isPolar) {
                    val adjustedDomain = (coordProvider as PolarCoordProvider)
                        .withHScaleContinuous(hScaleProto.isContinuous)
                        .adjustDomain(DoubleRectangle(xDomain, yDomain))

                    PolarFrameOfReferenceProvider(
                        plotContext, hScaleProto,
                        vScaleProto,
                        adjustedDomain,
                        flipAxis,
                        theme,
                        marginsLayout,
                        domainByMargin
                    )
                } else {
                    val adjustedDomain = coordProvider.adjustDomain(DoubleRectangle(xDomain, yDomain))
                    SquareFrameOfReferenceProvider(
                        plotContext, hScaleProto,
                        vScaleProto,
                        adjustedDomain,
                        flipAxis, hAxisPosition,
                        vAxisPosition,
                        theme,
                        marginsLayout,
                        domainByMargin
                    )
                }
            }
        }
    }
}
