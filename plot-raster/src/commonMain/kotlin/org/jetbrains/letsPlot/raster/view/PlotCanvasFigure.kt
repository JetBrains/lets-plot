/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.view

import org.jetbrains.letsPlot.commons.event.MouseEventPeer
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.registration.CompositeRegistration
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.core.canvas.CanvasPeer
import org.jetbrains.letsPlot.core.canvas.Context2d
import org.jetbrains.letsPlot.core.canvasFigure.CanvasFigure2
import org.jetbrains.letsPlot.core.interact.event.ToolEventDispatcher
import org.jetbrains.letsPlot.core.util.sizing.SizingPolicy
import org.jetbrains.letsPlot.raster.builder.MonolithicCanvas
import org.jetbrains.letsPlot.raster.builder.ViewModel
import kotlin.math.ceil

@Deprecated("Migrate to PlotCanvasFigure and CanvasPane", replaceWith = ReplaceWith("PlotCanvasFigure"))
typealias PlotCanvasFigure2 = PlotCanvasFigure

class PlotCanvasFigure : CanvasFigure2 {
    fun setRenderingHint(key: Any, value: Any) {
        plotSvgFigure.setRenderingHint(key, value)
    }
    override val mouseEventPeer: MouseEventPeer = MouseEventPeer()
    override val size: Vector get() {
        val (w, h) = sizingPolicy.resize(
            figureSizeDefault = plotSvgFigure.size.toDoubleVector(),
            containerSize = containerSize
        )

        return Vector(ceil(w).toInt(), ceil(h).toInt())
    }
    private val plotSvgFigure: SvgCanvasFigure = SvgCanvasFigure().also {
        it.mouseEventPeer.addEventSource(mouseEventPeer)
    }

    private var eventReg: Registration = Registration.EMPTY
    private var processedSpec: Map<String, Any>? = null
    private var sizingPolicy: SizingPolicy = SizingPolicy.keepFigureDefaultSize()
    private var computationMessagesHandler: (List<String>) -> Unit = { _ -> }

    private var viewModel: ViewModel? = null
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

    fun onHrefClick(handler: (href: String) -> Unit) {
        plotSvgFigure.onHrefClick(handler)
    }

    override fun paint(context2d: Context2d) {
        plotSvgFigure.paint(context2d)
    }

    override fun onRepaintRequested(listener: () -> Unit): Registration {
        return plotSvgFigure.onRepaintRequested(listener)
    }

    override fun mapToCanvas(canvasPeer: CanvasPeer): Registration {
        val reg = CompositeRegistration(
            plotSvgFigure.mapToCanvas(canvasPeer),
            Registration.onRemove {
                // Do not pass reference to the viewModelReg - it changes on CanvasControl resize or plot spec update.
                // With closure, we ensure that the current viewModelReg is disposed.
                viewModel?.dispose()
                eventReg.dispose()
            }
        )

        buildPlotSvg()

        return reg
    }

    @Suppress("unused") // for lets-plot-compose
    val toolEventDispatcher: ToolEventDispatcher? get() = viewModel?.toolEventDispatcher

    override fun resize(width: Number, height: Number) {
        containerSize = DoubleVector(width.toDouble(), height.toDouble())
        buildPlotSvg()
    }

    private fun buildPlotSvg() {
        val processedSpec = processedSpec ?: return

        eventReg.dispose()
        viewModel?.dispose()

        val vm = MonolithicCanvas.buildViewModelFromProcessedSpecs(
            plotSpec = processedSpec,
            sizingPolicy = sizingPolicy,
            containerSize = containerSize,
            computationMessagesHandler = computationMessagesHandler
        )

        plotSvgFigure.svgSvgElement = vm.svg
        eventReg = vm.eventDispatcher.addEventSource(mouseEventPeer)

        viewModel = vm
    }

    override fun isReady(): Boolean {
        return plotSvgFigure.isReady()
    }

    override fun onReady(listener: () -> Unit): Registration {
        return plotSvgFigure.onReady(listener)
    }
}
