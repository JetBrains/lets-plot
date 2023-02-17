/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.plot.builder.FigureBuildInfo
import jetbrains.datalore.plot.builder.GeomLayer
import jetbrains.datalore.plot.builder.PlotSvgRoot
import jetbrains.datalore.plot.builder.assemble.PlotAssembler
import jetbrains.datalore.plot.builder.layout.figure.FigureLayoutInfo
import jetbrains.datalore.plot.builder.layout.figure.plot.PlotFigureLayoutInfo

internal class PlotFigureBuildInfo constructor(
    private val plotAssembler: PlotAssembler,
    private val processedPlotSpec: Map<String, Any>,
    override val bounds: DoubleRectangle,
    override val computationMessages: List<String>,
) : FigureBuildInfo {

    override val containsLiveMap: Boolean = plotAssembler.containsLiveMap

    override val layoutInfo: FigureLayoutInfo
        get() = _layoutInfo

    private lateinit var _layoutInfo: PlotFigureLayoutInfo
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
        return if (this::_layoutInfo.isInitialized) {
            val plotSvgComponent = plotAssembler.createPlot(_layoutInfo)
            PlotSvgRoot(
                plotSvgComponent,
                liveMapCursorServiceConfig = if (containsLiveMap) liveMapCursorServiceConfig else null,
                bounds.origin
            )
        } else {
            layoutedByOuterSize().createSvgRoot()
        }
    }

    override fun withBounds(bounds: DoubleRectangle): PlotFigureBuildInfo {
        return makeCopy(bounds)
    }

    override fun layoutedByOuterSize(): PlotFigureBuildInfo {
        val outerSize = bounds.dimension
        val layoutInfo = plotAssembler.layoutByOuterSize(outerSize)
        return makeCopy().apply {
            this._layoutInfo = layoutInfo
        }
    }

    override fun layoutedByGeomBounds(geomBounds: DoubleRectangle): PlotFigureBuildInfo {
        val layoutInfo = plotAssembler.layoutByGeomSize(geomBounds.dimension)
        val oldCenter = geomBounds.center
        val newCenter = layoutInfo.geomAreaBounds.center
        val delta = newCenter.subtract(oldCenter)
        val newOrigin = this.bounds.origin.subtract(delta)
        val newSize = layoutInfo.figureLayoutedBounds.dimension
        val newBounds = DoubleRectangle(newOrigin, newSize)

        return makeCopy(newBounds).apply {
            this._layoutInfo = layoutInfo
        }
    }

    private fun makeCopy(newBounds: DoubleRectangle? = null): PlotFigureBuildInfo {
        val newBuildInfo = PlotFigureBuildInfo(
            plotAssembler,
            processedPlotSpec,
            newBounds ?: this.bounds,
            computationMessages,
        )

        if (this::liveMapCursorServiceConfig.isInitialized) {
            newBuildInfo.liveMapCursorServiceConfig = liveMapCursorServiceConfig
        }

        return newBuildInfo
    }
}