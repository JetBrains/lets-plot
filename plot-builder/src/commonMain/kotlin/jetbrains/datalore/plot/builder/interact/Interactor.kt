/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.registration.CompositeRegistration
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.plot.FeatureSwitch
import jetbrains.datalore.plot.base.interact.GeomTargetLocator
import jetbrains.datalore.plot.builder.event.MouseEventPeer
import jetbrains.datalore.plot.builder.interact.ui.EventsManager
import jetbrains.datalore.plot.builder.theme.Theme
import jetbrains.datalore.vis.svg.SvgNode

internal class Interactor(
    decorationLayer: SvgNode,
    mouseEventPeer: MouseEventPeer,
    plotSize: DoubleVector,
    flippedAxis: Boolean,
    theme: Theme,
) : PlotInteractor {
    private val reg = CompositeRegistration()
    private val eventsManager: EventsManager
    private val tooltipRenderer: TooltipRenderer
    private val viewToolboxRenderer: ViewToolboxRenderer?

    init {
        eventsManager = EventsManager()
        reg.add(Registration.from(eventsManager))
        eventsManager.setEventSource(mouseEventPeer)

        tooltipRenderer = TooltipRenderer(
            decorationLayer,
            flippedAxis,
            plotSize,
            theme.axisX(flippedAxis),
            theme.axisY(flippedAxis),
            mouseEventPeer
        )
        reg.add(Registration.from(tooltipRenderer))

        if (FeatureSwitch.PLOT_VIEW_TOOLBOX) {
            viewToolboxRenderer = ViewToolboxRenderer(decorationLayer, plotSize, eventsManager)
            reg.add(Registration.from(viewToolboxRenderer))
        } else {
            viewToolboxRenderer = null
        }
    }

    override fun onTileAdded(
        geomBounds: DoubleRectangle,
        tooltipBounds: PlotTooltipBounds,
        targetLocators: List<GeomTargetLocator>,
    ) {
        tooltipRenderer.addTileInfo(geomBounds, tooltipBounds, targetLocators)
    }

    override fun onViewReset(function: () -> Unit) {
        viewToolboxRenderer?.onViewReset(function)
    }

    override fun onViewZoomIn(function: (DoubleVector) -> Unit) {
        viewToolboxRenderer?.onViewZoomIn(function)
    }

    override fun onViewZoomArea(function: (DoubleRectangle) -> Unit) {
        viewToolboxRenderer?.onViewZoomArea(function)
    }

    override fun onViewZoomOut(function: (DoubleVector) -> Unit) {
        viewToolboxRenderer?.onViewZoomOut(function)
    }

    override fun onViewPanning(function: (DoubleVector) -> Unit) {
        viewToolboxRenderer?.onViewPanning(function)
    }

    override fun dispose() {
        reg.dispose()
    }
}
