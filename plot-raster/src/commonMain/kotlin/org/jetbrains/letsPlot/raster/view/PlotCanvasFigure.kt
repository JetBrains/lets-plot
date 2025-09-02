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

class PlotCanvasFigure : CanvasFigure {
    private var processedSpec: Map<String, Any>? = null
    private var sizingPolicy: SizingPolicy = SizingPolicy.keepFigureDefaultSize()
    private var computationMessagesHandler: (List<String>) -> Unit = { _ -> }

    private var canvasControl: CanvasControl? = null
    private var viewModelReg = Registration.EMPTY
    private val plotSvgFigure: SvgCanvasFigure = SvgCanvasFigure()

    fun update(
        processedSpec: Map<String, Any>,
        sizingPolicy: SizingPolicy,
        computationMessagesHandler: (List<String>) -> Unit
    ) {
        this.processedSpec = processedSpec
        this.sizingPolicy = sizingPolicy
        this.computationMessagesHandler = computationMessagesHandler

        buildPlotSvg()
    }

    override fun bounds(): ReadableProperty<Rectangle> {
        return plotSvgFigure.bounds()
    }

    override fun mapToCanvas(canvasControl: CanvasControl): Registration {
        this.canvasControl = canvasControl
        val reg = CompositeRegistration(
            plotSvgFigure.mapToCanvas(canvasControl),
            canvasControl.onResize { buildPlotSvg() },
            Registration.onRemove {
                // Do not pass reference to the viewModelReg - it changes on CanvasControl resize or plot spec update.
                // With closure, we ensure that the current viewModelReg is disposed.
                viewModelReg.dispose()
            }
        )

        buildPlotSvg()

        return reg
    }

    private fun buildPlotSvg() {
        val processedSpec = processedSpec ?: return

        viewModelReg.dispose()

        val viewModel = MonolithicCanvas.buildViewModelFromProcessedSpecs(
            plotSpec = processedSpec,
            sizingPolicy = sizingPolicy,
            containerSize = canvasControl?.size?.toDoubleVector(), // ok when sizingPolicy is independent on container size
            computationMessagesHandler = computationMessagesHandler
        )

        plotSvgFigure.svgSvgElement = viewModel.svg

        viewModelReg = CompositeRegistration(
            Registration.from(viewModel),
            canvasControl?.let { viewModel.eventDispatcher.addEventSource(it) } ?: Registration.EMPTY
        )
    }
}
