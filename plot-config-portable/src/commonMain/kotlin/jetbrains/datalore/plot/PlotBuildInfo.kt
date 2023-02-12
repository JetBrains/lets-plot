/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.unsupported.UNSUPPORTED
import jetbrains.datalore.plot.builder.GeomLayer
import jetbrains.datalore.plot.builder.PlotSvgRoot
import jetbrains.datalore.plot.builder.assemble.PlotAssembler
import jetbrains.datalore.plot.builder.config.FigureBuildInfo

class PlotBuildInfo constructor(
    private val plotAssembler: PlotAssembler,
    private val processedPlotSpec: Map<String, Any>,
    override val bounds: DoubleRectangle,
    override val computationMessages: List<String>,
) : FigureBuildInfo {

    override val containsLiveMap: Boolean = plotAssembler.containsLiveMap

    private lateinit var liveMapCursorServiceConfig: Any

    /**
     * This method should be called before 'createSvgRoot()'
     */
    override fun injectLiveMapProvider(f: (tiles: List<List<GeomLayer>>, spec: Map<String, Any>) -> Any) {
        if (containsLiveMap) {
            val listOfTiles = plotAssembler.coreLayersByTile
            liveMapCursorServiceConfig = f(listOfTiles, processedPlotSpec)
        }
    }

    override fun createSvgRoot(): PlotSvgRoot {
        val plotSvgComponent = plotAssembler.createPlot(bounds.dimension)
        return PlotSvgRoot(
            plotSvgComponent,
            liveMapCursorServiceConfig = if (containsLiveMap) liveMapCursorServiceConfig else null,
            bounds.origin
        )
    }

    override fun withBounds(bounds: DoubleRectangle): PlotBuildInfo {
        val newBuildInfo = PlotBuildInfo(
            plotAssembler,
            processedPlotSpec,
            bounds,
            computationMessages,
        )

        if (this::liveMapCursorServiceConfig.isInitialized) {
            newBuildInfo.liveMapCursorServiceConfig = liveMapCursorServiceConfig
        }

        return newBuildInfo
    }

    override fun toLayouted(): FigureBuildInfo {
        UNSUPPORTED()
    }
}