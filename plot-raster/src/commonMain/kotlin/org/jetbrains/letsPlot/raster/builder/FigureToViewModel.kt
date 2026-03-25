/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.builder

import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.core.plot.builder.GeomLayer
import org.jetbrains.letsPlot.core.plot.builder.PlotContainer
import org.jetbrains.letsPlot.core.plot.builder.PlotSvgRoot
import org.jetbrains.letsPlot.core.plot.builder.buildinfo.FigureBuildInfo
import org.jetbrains.letsPlot.core.plot.builder.subPlots.CompositeFigureSvgRoot

import org.jetbrains.letsPlot.core.plot.livemap.CursorServiceConfig
import org.jetbrains.letsPlot.core.plot.livemap.LiveMapProviderUtil

internal object FigureToViewModel {
    fun eval(buildInfo: FigureBuildInfo): ViewModel {
        @Suppress("NAME_SHADOWING")
        val buildInfo = buildInfo.layoutedByOuterSize()

        buildInfo.injectLiveMapProvider { tiles: List<List<GeomLayer>>, spec: Map<String, Any> ->
            val cursorServiceConfig = CursorServiceConfig()
            LiveMapProviderUtil.injectLiveMapProvider(tiles, spec, cursorServiceConfig)
            cursorServiceConfig
        }

        return when (val svgRoot = buildInfo.createSvgRoot()) {
            is PlotSvgRoot -> processPlotFigure(svgRoot)
            is CompositeFigureSvgRoot -> processCompositeFigure(svgRoot).also {
                it.assembleAsRoot()
            }

            else -> error("Unsupported figure: ${svgRoot::class.simpleName}")
        }
    }

    private fun processCompositeFigure(svgRoot: CompositeFigureSvgRoot): CompositeFigureModel {
        svgRoot.ensureContentBuilt()

        val compositeModel = CompositeFigureModel(svgRoot.svg)

        for (childSvg in svgRoot.elements) {
            val childBounds = childSvg.bounds.add(svgRoot.bounds.origin)

            childSvg.svg.x().set(childBounds.left)
            childSvg.svg.y().set(childBounds.top)

            val childModel = when (childSvg) {
                is CompositeFigureSvgRoot -> processCompositeFigure(childSvg)
                is PlotSvgRoot -> processPlotFigure(childSvg)
                else -> error("Unsupported figure: ${svgRoot::class.simpleName}")
            }

            // Use childSvg.bounds (local to this composite) for mouse event dispatch,
            // not childBounds (which includes the parent's origin offset).
            // Events arriving here are already translated to local coordinates
            // by the parent's ChildMouseEventSource.
            compositeModel.addChild(childModel, childSvg.bounds)
        }
        return compositeModel
    }

    private fun processPlotFigure(svgRoot: PlotSvgRoot): SinglePlotModel {
        val plotContainer = PlotContainer(svgRoot)

        val plotModel = SinglePlotModel(
            svg = svgRoot.svg,
            toolEventDispatcher = plotContainer.toolEventDispatcher,
            registration = Registration.from(plotContainer)
        )
        plotContainer.mouseEventPeer.addEventSource(plotModel.mouseEventPeer)

        return plotModel
    }
}
