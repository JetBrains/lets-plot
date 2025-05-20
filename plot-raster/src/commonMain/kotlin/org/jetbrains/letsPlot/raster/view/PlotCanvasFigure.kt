/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.view

import org.jetbrains.letsPlot.commons.geometry.Rectangle
import org.jetbrains.letsPlot.commons.intern.observable.property.ReadableProperty
import org.jetbrains.letsPlot.commons.intern.observable.property.ValueProperty
import org.jetbrains.letsPlot.commons.registration.CompositeRegistration
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.core.canvas.CanvasControl
import org.jetbrains.letsPlot.core.canvasFigure.CanvasFigure
import org.jetbrains.letsPlot.core.util.sizing.SizingPolicy
import org.jetbrains.letsPlot.raster.builder.MonolithicCanvas

class PlotCanvasFigure internal constructor(
    processedSpec: Map<String, Any>,
    sizingPolicy: SizingPolicy,
    computationMessagesHandler: (List<String>) -> Unit
) : CanvasFigure {
    private val vm = MonolithicCanvas.buildPlotFromProcessedSpecs(processedSpec, sizingPolicy, computationMessagesHandler)

    private var plotSvgFigure: SvgCanvasFigure? = null

    val plotWidth = vm.bounds.dimension.x
    val plotHeight = vm.bounds.dimension.y

    override fun bounds(): ReadableProperty<Rectangle> {
        return plotSvgFigure?.bounds() ?: ValueProperty(Rectangle(0, 0, 0, 0))
    }

    override fun mapToCanvas(canvasControl: CanvasControl): Registration {
        val plotSvg = vm.svg
        val plotEventPeer = vm.eventDispatcher

        plotSvgFigure = SvgCanvasFigure(plotSvg)
        plotEventPeer.dispatchFrom(canvasControl)

        val reg = CompositeRegistration(
            Registration.from(vm),
            plotSvgFigure!!.mapToCanvas(canvasControl)
        )

        return reg
    }
}
