/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.core.plot.builder.FigureBuildInfo
import org.jetbrains.letsPlot.core.plot.builder.GeomLayer
import org.jetbrains.letsPlot.core.plot.builder.PlotSvgRoot
import org.jetbrains.letsPlot.core.plot.builder.assemble.PlotAssembler
import org.jetbrains.letsPlot.core.plot.builder.layout.figure.FigureLayoutInfo
import org.jetbrains.letsPlot.core.plot.builder.layout.figure.plot.PlotFigureLayoutInfo

internal class PlotFigureBuildInfo constructor(
    private val plotAssembler: PlotAssembler,
    private val processedPlotSpec: Map<String, Any>,
    override val bounds: DoubleRectangle,
    override val computationMessages: List<String>,
) : FigureBuildInfo {

    override val isComposite: Boolean = false

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
        check(this::_layoutInfo.isInitialized) { "Plot figure is not layouted." }
        val plotSvgComponent = plotAssembler.createPlot(_layoutInfo)
        return PlotSvgRoot(
            plotSvgComponent,
            liveMapCursorServiceConfig = if (containsLiveMap) liveMapCursorServiceConfig else null,
            bounds.origin
        )
    }

    override fun withBounds(bounds: DoubleRectangle): PlotFigureBuildInfo {
        return if (bounds == this.bounds) {
            this
        } else {
            // this drops 'layout info' if initialized.
            makeCopy(bounds)
        }
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