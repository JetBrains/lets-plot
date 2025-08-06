/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.view

import org.jetbrains.letsPlot.commons.event.MouseEventSource
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.registration.CompositeRegistration
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.core.canvas.CanvasPeer
import org.jetbrains.letsPlot.core.canvas.Context2d
import org.jetbrains.letsPlot.core.canvasFigure.CanvasFigure
import org.jetbrains.letsPlot.core.util.sizing.SizingPolicy
import org.jetbrains.letsPlot.raster.builder.MonolithicCanvas
import org.jetbrains.letsPlot.raster.builder.ViewModel

class PlotCanvasFigure : CanvasFigure {
    private var mouseEventSource: MouseEventSource? = null
    private var processedSpec: Map<String, Any>? = null
    private var sizingPolicy: SizingPolicy = SizingPolicy.keepFigureDefaultSize()
    private var computationMessagesHandler: (List<String>) -> Unit = { _ -> }

    private var viewModel: ViewModel? = null
    private var viewModelReg = Registration.EMPTY
    private var containerSize: DoubleVector = DoubleVector.ZERO

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

    fun setMouseEventSource(mouseEventSource: MouseEventSource) {
        this.mouseEventSource = mouseEventSource
    }

    override val size: Vector get() = plotSvgFigure.size

    override fun mapToCanvas(canvasPeer: CanvasPeer): Registration {
        val reg = CompositeRegistration(
            plotSvgFigure.mapToCanvas(canvasPeer),
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

        viewModel?.dispose()

        // It's fine to build a view model without a canvasControl.
        // Size policy may work with a null container size.
        val vm = MonolithicCanvas.buildViewModelFromProcessedSpecs(
            plotSpec = processedSpec,
            sizingPolicy = sizingPolicy,
            containerSize = containerSize,
            computationMessagesHandler = computationMessagesHandler
        )

        plotSvgFigure.svgSvgElement = vm.svg
        mouseEventSource?.let { vm.eventDispatcher.addEventSource(it) }

        viewModel = vm
    }

    fun resize(width: Number, height: Number) {
        containerSize = DoubleVector(width.toDouble(), height.toDouble())
        buildPlotSvg()
    }
}
