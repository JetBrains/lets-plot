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
import org.jetbrains.letsPlot.core.util.sizing.SizingMode
import org.jetbrains.letsPlot.core.util.sizing.SizingPolicy
import org.jetbrains.letsPlot.raster.builder.MonolithicCanvas
import org.jetbrains.letsPlot.raster.builder.ViewModel

class PlotCanvasFigure internal constructor(
    private val processedSpec: Map<String, Any>,
    private val sizingPolicy: SizingPolicy,
    private val computationMessagesHandler: (List<String>) -> Unit
) : CanvasFigure {

    val preferredWidth: Int? // null if forced to fit the container
    val preferredHeight: Int? // null if forced to fit the container

    val plotWidth: Int get() = plotSvgFigure.svgSvgElement.width().get()?.toInt() ?: 0
    val plotHeight: Int get() = plotSvgFigure.svgSvgElement.height().get()?.toInt() ?: 0

    private var viewModel: ViewModel = MonolithicCanvas.buildPlotFromProcessedSpecs(
        plotSpec = processedSpec,
        sizingPolicy = sizingPolicy,
        containerSize = null, // no container size at this point
        computationMessagesHandler = computationMessagesHandler
    )
    private val plotSvgFigure: SvgCanvasFigure = SvgCanvasFigure(viewModel.svg)
    private var plotReg = CompositeRegistration()

    init {
        if (sizingPolicy.widthMode == SizingMode.FIT && sizingPolicy.heightMode == SizingMode.FIT) {
            preferredWidth = null
            preferredHeight = null
        } else {
            preferredWidth = viewModel.svg.width().get()?.toInt() ?: error("Width is not specified in the plot spec")
            preferredHeight = viewModel.svg.height().get()?.toInt() ?: error("Height is not specified in the plot spec")
        }

    }

    override fun bounds(): ReadableProperty<Rectangle> {
        return plotSvgFigure.bounds()
    }

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

        viewModel = MonolithicCanvas.buildPlotFromProcessedSpecs(
            plotSpec = processedSpec,
            sizingPolicy = sizingPolicy,
            containerSize = canvasControl.size.toDoubleVector(),
            computationMessagesHandler = computationMessagesHandler
        )

        plotSvgFigure.svgSvgElement = viewModel.svg

        plotReg = CompositeRegistration(
            Registration.from(viewModel),
            viewModel.eventDispatcher.addEventSource(canvasControl)
        )
    }
}
