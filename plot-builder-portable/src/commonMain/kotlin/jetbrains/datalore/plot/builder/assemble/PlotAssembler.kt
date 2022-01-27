/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.assemble

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.base.ScaleMapper
import jetbrains.datalore.plot.builder.*
import jetbrains.datalore.plot.builder.coord.CoordProvider
import jetbrains.datalore.plot.builder.layout.LegendBoxInfo
import jetbrains.datalore.plot.builder.layout.LiveMapTileLayout
import jetbrains.datalore.plot.builder.layout.PlotLayout
import jetbrains.datalore.plot.builder.theme.Theme

class PlotAssembler private constructor(
    val layersByTile: List<List<GeomLayer>>,
    private val scaleXProto: Scale<Double>,
    private val scaleYProto: Scale<Double>,
    private val scaleMappers: Map<Aes<*>, ScaleMapper<*>>,
    private val coordProvider: CoordProvider,
    private val theme: Theme
) {

    val containsLiveMap: Boolean = layersByTile.flatten().any(GeomLayer::isLiveMap)

    var facets: PlotFacets = PlotFacets.undefined()
    var title: String? = null
    var guideOptionsMap: Map<Aes<*>, GuideOptions> = HashMap()

    private var legendsEnabled = true
    private var interactionsEnabled = true


    private fun hasLayers(): Boolean {
        for (tileLayers in layersByTile) {
            if (tileLayers.isNotEmpty()) {
                return true
            }
        }
        return false
    }

    fun createPlot(): PlotSvgComponent {
        require(hasLayers()) { "No layers in plot" }

        val legendsBoxInfos = when {
            legendsEnabled -> PlotAssemblerUtil.createLegends(
                layersByTile,
                scaleMappers,
                guideOptionsMap,
                theme.legend()
            )
            else -> emptyList()
        }

        return if (containsLiveMap) {
            // build 'live map' plot:
            //  - skip X/Y scale training
            //  - ignore coord provider
            //  - plot layout without axes
            val plotLayout = PlotAssemblerUtil.createPlotLayout(LiveMapTileLayout(), facets, theme.facets())
            val fOrProvider = BogusFrameOfReferenceProvider()
            createPlot(fOrProvider, plotLayout, legendsBoxInfos)
        } else {
            val flipAxis = coordProvider.flipAxis
            val (xDomain, yDomain) = PositionalScalesUtil.computePlotXYTransformedDomains(
                layersByTile,
                scaleXProto,
                scaleYProto
            )
            val (hDomain, vDomain) = coordProvider.adjustDomains(
                hDomain = if (flipAxis) yDomain else xDomain,
                vDomain = if (flipAxis) xDomain else yDomain
            )
            val (hScaleProto, vScaleProto) = when (flipAxis) {
                true -> scaleYProto to scaleXProto
                else -> scaleXProto to scaleYProto
            }
            val fOrProvider = SquareFrameOfReferenceProvider(
                hScaleProto, vScaleProto,
                hDomain, vDomain,
                flipAxis,
                theme
            )
            val plotLayout = PlotAssemblerUtil.createPlotLayout(fOrProvider.createTileLayout(), facets, theme.facets())
            createPlot(fOrProvider, plotLayout, legendsBoxInfos)
        }
    }

    private fun createPlot(
        fOrProvider: TileFrameOfReferenceProvider,
        plotLayout: PlotLayout,
        legendBoxInfos: List<LegendBoxInfo>
    ): PlotSvgComponent {

        return PlotSvgComponent(
            title = title,
            layersByTile = layersByTile,
            plotLayout = plotLayout,
            frameOfReferenceProvider = fOrProvider,
            coordProvider = coordProvider,
            legendBoxInfos = legendBoxInfos,
            interactionsEnabled = interactionsEnabled,
            theme = theme
        )
    }

    fun disableLegends() {
        legendsEnabled = false
    }

    fun disableInteractions() {
        interactionsEnabled = false
    }

    companion object {
        // Note: 'singleTile' is only used in demos.
        fun singleTile(
            plotLayers: List<GeomLayer>,
            scaleXProto: Scale<Double>,
            scaleYProto: Scale<Double>,
            scaleMappersNP: Map<Aes<*>, ScaleMapper<*>>,
            coordProvider: CoordProvider,
            theme: Theme
        ): PlotAssembler {
            val layersByTile = ArrayList<List<GeomLayer>>()
            layersByTile.add(plotLayers)
            return multiTile(
                layersByTile,
                scaleXProto,
                scaleYProto,
                scaleMappersNP,
                coordProvider,
                theme
            )
        }

        fun multiTile(
            layersByTile: List<List<GeomLayer>>,
            scaleXProto: Scale<Double>,
            scaleYProto: Scale<Double>,
            scaleMappersNP: Map<Aes<*>, ScaleMapper<*>>,
            coordProvider: CoordProvider,
            theme: Theme
        ): PlotAssembler {
            return PlotAssembler(
                layersByTile,
                scaleXProto,
                scaleYProto,
                scaleMappersNP,
                coordProvider,
                theme
            )
        }
    }
}
