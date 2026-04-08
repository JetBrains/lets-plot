/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder

import org.jetbrains.letsPlot.commons.event.MouseEventPeer
import org.jetbrains.letsPlot.commons.registration.CompositeRegistration
import org.jetbrains.letsPlot.commons.registration.Disposable
import org.jetbrains.letsPlot.core.canvas.CanvasDrawable
import org.jetbrains.letsPlot.core.interact.event.ToolEventDispatcher
import org.jetbrains.letsPlot.core.interact.event.UnsupportedToolEventDispatcher
import org.jetbrains.letsPlot.core.plot.builder.interact.PlotToolEventDispatcher
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgSvgElement

class PlotContainer(
    val svgRoot: PlotSvgRoot,
    inDeck: Boolean = false,
    isTopmost: Boolean = true,   // only for plots in a deck, ignored otherwise.
) : Disposable {

    val svg: SvgSvgElement
        get() = svgRoot.svg

    val liveMapCanvasDrawables: List<CanvasDrawable>
        get() = svgRoot.liveMapCanvasDrawables

    val isLiveMap: Boolean
        get() = svgRoot.isLiveMap

    val mouseEventPeer: MouseEventPeer
        get() = plot.mouseEventPeer

    private val plot: PlotSvgComponent = svgRoot.plot
    private var registrations = CompositeRegistration()

    val toolEventDispatcher: ToolEventDispatcher

    init {
        if (plot.interactionsEnabled) {
            val plotInteractor = PlotInteractor(
                decorationLayer = svgRoot.decorationLayer,
                mouseEventPeer = mouseEventPeer,
                plotSize = plot.figureSize,    // ToDo: svgRoot.bounds.dimension
                flippedAxis = plot.flippedAxis,
                theme = plot.theme,
                styleSheet = plot.styleSheet,
                plotContext = plot.plotContext
            )
            plot.interactor = plotInteractor

            toolEventDispatcher = PlotToolEventDispatcher(
                plotInteractor,
                internalDebounce = !inDeck,
                showSelectionFeedback = !inDeck || isTopmost
            )

        } else {
            toolEventDispatcher = UnsupportedToolEventDispatcher()
        }

        svgRoot.ensureContentBuilt()
    }

    override fun dispose() {
        registrations.remove()
        svgRoot.clearContent()
    }
}
