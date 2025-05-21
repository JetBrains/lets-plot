/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.view

import org.jetbrains.letsPlot.commons.geometry.Rectangle
import org.jetbrains.letsPlot.commons.intern.observable.property.ReadableProperty
import org.jetbrains.letsPlot.commons.registration.CompositeRegistration
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.core.canvas.CanvasControl
import org.jetbrains.letsPlot.core.canvasFigure.CanvasFigure
import org.jetbrains.letsPlot.core.util.sizing.SizingPolicy
import org.jetbrains.letsPlot.raster.builder.MonolithicCanvas

class PlotCanvasFigure internal constructor(
    private val processedSpec: Map<String, Any>,
    private val sizingPolicy: SizingPolicy,
    private val computationMessagesHandler: (List<String>) -> Unit
) : CanvasFigure {
    val plotWidth: Int get() = plotSvgFigure.svgSvgElement.width().get()?.toInt() ?: 0
    val plotHeight: Int get() = plotSvgFigure.svgSvgElement.height().get()?.toInt() ?: 0

    override fun bounds(): ReadableProperty<Rectangle> {
        return plotSvgFigure.bounds()
    }

    private val plotSvgFigure: SvgCanvasFigure = SvgCanvasFigure()
    private var plotReg = CompositeRegistration()

    override fun mapToCanvas(canvasControl: CanvasControl): Registration {
        val reg = CompositeRegistration(
            plotSvgFigure.mapToCanvas(canvasControl),
            canvasControl.onResize {
                buildPlot(canvasControl)
            },
            // via closure because plotReg may change after resize
            object : Registration() {
                override fun doRemove() {
                    // via closure because plotReg may change after resize
                    // via closure because plotReg may change after resize
                    this@PlotCanvasFigure.plotReg.dispose() // via closure because plotReg may change after resize
                }
            }
        )

        buildPlot(canvasControl)

        return reg
    }

    private fun buildPlot(canvasControl: CanvasControl) {
        plotReg.dispose()

        val vm = MonolithicCanvas.buildPlotFromProcessedSpecs(
            plotSpec = processedSpec,
            sizingPolicy = sizingPolicy,
            containerSize = canvasControl.size.toDoubleVector(),
            computationMessagesHandler = computationMessagesHandler
        )

        plotSvgFigure.svgSvgElement = vm.svg


        plotReg = CompositeRegistration(
            Registration.from(vm),
            vm.eventDispatcher.addEventSource(canvasControl)
        )
    }
}
