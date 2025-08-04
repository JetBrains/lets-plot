/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.view

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.geometry.Rectangle
import org.jetbrains.letsPlot.commons.intern.observable.property.ReadableProperty
import org.jetbrains.letsPlot.commons.registration.CompositeRegistration
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.core.canvas.CanvasControl
import org.jetbrains.letsPlot.core.canvas.CanvasProvider
import org.jetbrains.letsPlot.core.canvas.Context2d
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
    private var containerSize: DoubleVector = DoubleVector.ZERO

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
        TODO("Not yet implemented")
    }

    override fun mapToCanvas(canvasProvider: CanvasProvider): Registration {
        val reg = CompositeRegistration(
            plotSvgFigure.mapToCanvas(canvasProvider),
            //canvasControl.onResize { buildPlotSvg() },
            Registration.onRemove {
                // Do not pass reference to the viewModelReg - it changes on CanvasControl resize or plot spec update.
                // With closure, we ensure that the current viewModelReg is disposed.
                viewModelReg.dispose()
            }
        )

        buildPlotSvg()

        return reg
    }

    override fun draw(context2d: Context2d) {
        plotSvgFigure.draw(context2d)
    }

    override fun onRepaintRequest(handler: () -> Unit): Registration {
        return plotSvgFigure.onRepaintRequest(handler)
    }

    private fun buildPlotSvg() {
        val processedSpec = processedSpec ?: return

        // It's fine to build a view model without a canvasControl.
        // Size policy may work with a null container size.
        val viewModel = MonolithicCanvas.buildViewModelFromProcessedSpecs(
            plotSpec = processedSpec,
            sizingPolicy = sizingPolicy,
            containerSize = containerSize,
            computationMessagesHandler = computationMessagesHandler
        )

        plotSvgFigure.svgSvgElement = viewModel.svg

        val canvasControl = canvasControl ?: return

        viewModelReg.dispose()
        viewModelReg = CompositeRegistration(
            Registration.from(viewModel),
            viewModel.eventDispatcher.addEventSource(canvasControl)
        )
    }

    fun resize(width: Number, height: Number) {
        containerSize = DoubleVector(width.toDouble(), height.toDouble())
        buildPlotSvg()
    }
}
