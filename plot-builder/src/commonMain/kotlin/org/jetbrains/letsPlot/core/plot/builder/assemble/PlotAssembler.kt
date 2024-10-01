/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.assemble

import org.jetbrains.letsPlot.commons.formatting.number.NumberFormat
import org.jetbrains.letsPlot.commons.formatting.string.StringFormat
import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.PlotContext
import org.jetbrains.letsPlot.core.plot.base.Scale
import org.jetbrains.letsPlot.core.plot.base.theme.ExponentFormat
import org.jetbrains.letsPlot.core.plot.base.theme.Theme
import org.jetbrains.letsPlot.core.plot.builder.FrameOfReferenceProvider
import org.jetbrains.letsPlot.core.plot.builder.GeomLayer
import org.jetbrains.letsPlot.core.plot.builder.MarginalLayerUtil
import org.jetbrains.letsPlot.core.plot.builder.PlotSvgComponent
import org.jetbrains.letsPlot.core.plot.builder.coord.CoordProvider
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
    facets: PlotFacets,
    private val xAxisPosition: AxisPosition,
    private val yAxisPosition: AxisPosition,
    private val theme: Theme,
    title: String? = null,
    subtitle: String? = null,
    caption: String? = null,
    guideOptionsMap: Map<GuideKey, GuideOptionsList> = HashMap(),
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
        plotContext = PlotAssemblerPlotContext(
            geomTiles,
            expFormat = extractExponentFormat(theme.exponentFormat)
        )

        val legendBoxInfos: List<LegendBoxInfo> = when {
            legendsEnabled -> PlotAssemblerUtil.createLegends(
                plotContext,
                geomTiles,
                geomTiles.mappersNP,
                guideOptionsMap,
                theme.legend(),
                theme.panel()
            )

            else -> emptyList()
        }

        val flipAxis = geomTiles.coordProvider.flipped

        val (hAxisPosition, vAxisPosition) = when (flipAxis) {
            true -> yAxisPosition.flip() to xAxisPosition.flip()
            false -> xAxisPosition to yAxisPosition
        }

        val scaleXByTile = geomTiles.scaleXByTile()
        val scaleYByTile = geomTiles.scaleYByTile()

        val transformedDomainsXYByTile: List<Pair<DoubleSpan, DoubleSpan>>? = when {
            geomTiles.containsLiveMap -> null
            else -> PositionalScalesUtil.computePlotXYTransformedDomains(
                geomTiles.coreLayersByTile(),
                scaleXByTile,
                scaleYByTile,
                facets,
                geomTiles.coordProvider
            )
        }

        frameProviderByTile = frameProviderByTile(
            coreLayersByTile = geomTiles.coreLayersByTile(),
            marginalLayersByTile = geomTiles.marginalLayersByTile(),
            coordProvider = geomTiles.coordProvider,
            scaleXProtoByTile = scaleXByTile,
            scaleYProtoByTile = scaleYByTile,
            rawXYTransformedDomainsByTile = transformedDomainsXYByTile,
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
            scaleXProtoByTile: List<Scale>,
            scaleYProtoByTile: List<Scale>,
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

            // Marginal layers.
            // Marginal layers share "marginal domain" and layout across all tiles.
            val domainByMarginByTile = marginalLayersByTile.mapIndexed { tileIndex, tileMarginalLayers ->
                MarginalLayerUtil.marginalDomainByMargin(
                    tileMarginalLayers,
                    scaleXProtoByTile[tileIndex],
                    scaleYProtoByTile[tileIndex],
                    coordProvider
                )
            }

            val marginsLayout: GeomMarginsLayout = GeomMarginsLayout.create(marginalLayersByTile[0])

            val (hScaleProtoByTile, vScaleProtoByTile) = when (flipAxis) {
                true -> scaleYProtoByTile to scaleXProtoByTile
                else -> scaleXProtoByTile to scaleYProtoByTile
            }

            // Create frame of reference provider for each tile.
            return domainsXYByTile.mapIndexed { tileIndex, (xDomain, yDomain) ->
                val adjustedDomain = coordProvider.adjustDomain(DoubleRectangle(xDomain, yDomain))

                if (coordProvider.isPolar) {
                    // FixMe: polar hack:
                    //   Need to coerce "data domain" because "flip" is not exactly what it seems.
                    //   When `theta = Y` --> flip=true
                    val adjustedDomainForPolar = adjustedDomain.flipIf(flipAxis)
                    PolarFrameOfReferenceProvider(
                        plotContext,
                        hScaleProtoByTile[tileIndex],
                        vScaleProtoByTile[tileIndex],
                        adjustedDomainForPolar,
                        flipAxis,
                        theme,
                        marginsLayout,
                        domainByMarginByTile[tileIndex]
                    )
                } else {
                    SquareFrameOfReferenceProvider(
                        plotContext,
                        hScaleProtoByTile[tileIndex],
                        vScaleProtoByTile[tileIndex],
                        adjustedDomain,
                        flipAxis, hAxisPosition,
                        vAxisPosition,
                        theme,
                        marginsLayout,
                        domainByMarginByTile[tileIndex]
                    )
                }
            }
        }

        fun extractExponentFormat(exponentFormat: ExponentFormat): StringFormat.ExponentFormat {
            val notationType = when (exponentFormat.notationType) {
                ExponentFormat.NotationType.E -> NumberFormat.ExponentNotationType.E
                ExponentFormat.NotationType.POW -> NumberFormat.ExponentNotationType.POW
                ExponentFormat.NotationType.POW_FULL -> NumberFormat.ExponentNotationType.POW_FULL
            }
            return StringFormat.ExponentFormat(notationType, exponentFormat.min, exponentFormat.max)
        }
    }
}
