/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

import org.jetbrains.letsPlot.commons.registration.CompositeRegistration
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.core.interact.event.ToolEventDispatcher
import org.jetbrains.letsPlot.core.interact.event.UnsupportedToolEventDispatcher
import org.jetbrains.letsPlot.core.plot.builder.buildinfo.FigureBuildInfo
import org.jetbrains.letsPlot.core.util.sizing.SizingPolicy
import org.jetbrains.letsPlot.platf.w3c.canvas.DomCanvasView
import org.jetbrains.letsPlot.raster.view.PlotCanvasDrawable
import org.w3c.dom.HTMLElement

internal class FigureToHtml(
    private val buildInfo: FigureBuildInfo,
    private val processedPlotSpec: Map<String, Any>,
    private val parentElement: HTMLElement,
    private val sizingPolicy: SizingPolicy,
) {
    fun eval(): Result {
        val size = buildInfo.layoutedByOuterSize().bounds.dimension

        parentElement.setAttribute(
            "style",
            "position: relative; width: ${size.x}px; height: ${size.y}px;"
        )

        val canvasView = DomCanvasView()
        canvasView.attachTo(parentElement)
        canvasView.setSize(size.x.toInt(), size.y.toInt())

        val plotDrawable = PlotCanvasDrawable()
        plotDrawable.update(processedPlotSpec, sizingPolicy) { }
        canvasView.content = plotDrawable

        return Result(
            plotDrawable.toolEventDispatcher ?: UnsupportedToolEventDispatcher(),
            CompositeRegistration(
                Registration.from(canvasView),
                Registration.onRemove {
                    while (parentElement.firstChild != null) {
                        parentElement.removeChild(parentElement.firstChild!!)
                    }
                }
            )
        )
    }

    data class Result(
        val toolEventDispatcher: ToolEventDispatcher,
        val figureRegistration: Registration
    )
}
