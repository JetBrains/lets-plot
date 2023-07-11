/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.livemap

import org.jetbrains.letsPlot.commons.event.MouseEvent
import org.jetbrains.letsPlot.commons.event.MouseEventSpec
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.observable.event.EventHandler
import org.jetbrains.letsPlot.commons.registration.Registration
import jetbrains.datalore.plot.MonolithicCommon
import jetbrains.datalore.plot.builder.PlotContainer
import jetbrains.datalore.plot.builder.PlotSvgRoot
import jetbrains.datalore.plot.builder.event.MouseEventPeer
import jetbrains.datalore.plot.livemap.LiveMapProviderUtil.injectLiveMapProvider
import jetbrains.datalore.plot.parsePlotSpec
import jetbrains.datalore.vis.canvas.AnimationProvider
import jetbrains.datalore.vis.canvas.CanvasControlDelegate
import jetbrains.livemap.config.LiveMapCanvasFigure

class LiveMapTestAdapter(
    plotSpec: String
) {
    private var mouseEventPeer: MouseEventPeer
    private val animationEventHandler: AnimationProvider.AnimationEventHandler

    init {
        val processSpecs = MonolithicCommon.processRawSpecs(parsePlotSpec(plotSpec), false)
        val buildResult = MonolithicCommon.buildPlotsFromProcessedSpecs(processSpecs, DoubleVector(600.0, 400.0))
        val successBuildResult = buildResult as MonolithicCommon.PlotsBuildResult.Success
        val buildInfo = successBuildResult.buildInfos.single().layoutedByOuterSize()
        buildInfo.injectLiveMapProvider { tiles, spec -> injectLiveMapProvider(tiles, spec, CursorServiceConfig()) }

        val svgRoot = buildInfo.createSvgRoot() as PlotSvgRoot
        val plotContainer = PlotContainer(svgRoot)
        mouseEventPeer = plotContainer.mouseEventPeer

        val fig = plotContainer.liveMapFigures.single() as LiveMapCanvasFigure
        var timerHandler: AnimationProvider.AnimationEventHandler? = null

        val canvasControl = object : CanvasControlDelegate(600, 400) {
            override fun createAnimationTimer(eventHandler: AnimationProvider.AnimationEventHandler): AnimationProvider.AnimationTimer {
                timerHandler = eventHandler
                return super.createAnimationTimer(eventHandler)
            }

            override fun addEventHandler(eventSpec: MouseEventSpec, eventHandler: EventHandler<MouseEvent>): Registration {
                return plotContainer.mouseEventPeer.addEventHandler(eventSpec, eventHandler)
            }
        }

        fig.mapToCanvas(canvasControl)
        animationEventHandler = timerHandler!!
    }

    fun dispatchTimerEvent(millisTime: Long) {
        animationEventHandler.onEvent(millisTime)
    }

    fun dispatchMouseEvent(spec: MouseEventSpec, event: MouseEvent) {
        mouseEventPeer.dispatch(spec, event)
    }

    fun getHoverObjects() {
        // TODO: access LiveMap from here
    }
}