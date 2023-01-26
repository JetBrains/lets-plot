/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.plot.builder.GeomLayer
import jetbrains.datalore.plot.builder.PlotSvgComponent
import jetbrains.datalore.plot.builder.assemble.PlotAssembler

internal class PlotFigureBuildInfo constructor(
    private val plotAssembler: PlotAssembler,
    private val processedPlotSpec: MutableMap<String, Any>,
    override val bounds: DoubleRectangle,
    override val computationMessages: List<String>,
) : FigureBuildInfo {
    override val containsLiveMap: Boolean = plotAssembler.containsLiveMap

    override fun createFigure(): PlotSvgComponent {
        return plotAssembler.createPlot()
    }

    override fun forEachPlot(f: (tiles: List<List<GeomLayer>>, spec: Map<String, Any>) -> Unit) {
        val listOfTiles = plotAssembler.coreLayersByTile
        f(listOfTiles, processedPlotSpec)
    }

    override fun forEachPlot(f: (assembler: PlotAssembler) -> Unit) {
        f(plotAssembler)
    }

    fun withBounds(r: DoubleRectangle): PlotFigureBuildInfo {
        return PlotFigureBuildInfo(
            plotAssembler,
            processedPlotSpec,
            r,
            computationMessages,
        )
    }
}