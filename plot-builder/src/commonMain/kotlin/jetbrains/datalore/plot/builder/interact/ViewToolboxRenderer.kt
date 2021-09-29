/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact

import jetbrains.datalore.base.event.MouseEvent
import jetbrains.datalore.base.event.MouseEventSpec.*
import jetbrains.datalore.base.gcommon.collect.Comparables.max
import jetbrains.datalore.base.gcommon.collect.Comparables.min
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.geometry.DoubleVector.Companion.ZERO
import jetbrains.datalore.base.registration.CompositeRegistration
import jetbrains.datalore.base.registration.Disposable
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.builder.interact.ui.EventsManager
import jetbrains.datalore.plot.builder.interact.ui.ToggleButtonControl
import jetbrains.datalore.plot.builder.interact.ui.ToolboxControl
import jetbrains.datalore.vis.svg.SvgGElement
import jetbrains.datalore.vis.svg.SvgNode
import jetbrains.datalore.vis.svg.SvgRectElement

internal class ViewToolboxRenderer(
    decorationLayer: SvgNode,
    plotSize: DoubleVector,
    eventsManager: EventsManager,
) : Disposable {
    private var onViewZoomAreaHandler: ((DoubleRectangle) -> Unit)? = null
    private var onViewZoomInHandler: ((DoubleVector) -> Unit)? = null
    private var onViewZoomOutHandler: ((DoubleVector) -> Unit)? = null
    private var onViewPanningHandler: ((DoubleVector) -> Unit)? = null
    private var onViewResetHandler: (() -> Unit)? = null
    private val toolboxLayer: SvgGElement
    private var tool: Tool? = null
        set(value) {
            field?.dispose()
            field = value
        }

    init {
        val toolbox = ToolboxControl(
            listOf(
                ToggleButtonControl(
                    rectContent(Color.LIGHT_GREEN),
                    rectContent(Color.GREEN),
                ).apply {
                    onToggleClick { isChecked ->
                        tool = when (isChecked) {
                            true -> PanTool(decorationLayer, eventsManager)
                            false -> null
                        }
                        println("PanTool enabled: $isChecked")
                    }
                },
                ToggleButtonControl(
                    rectContent(Color.LIGHT_BLUE),
                    rectContent(Color.BLUE),
                ).apply {
                    onToggleClick { isChecked ->
                        tool = when (isChecked) {
                            true -> ZoomTool(decorationLayer, eventsManager)
                            false -> null
                        }
                        println("ZoomTool enabled: $isChecked")
                    }
                },
                ToggleButtonControl(
                    rectContent(Color.LIGHT_GRAY),
                    rectContent(Color.GRAY),
                ).apply {
                    onClick {
                        onViewResetHandler?.invoke()
                    }
                }
            ))

        toolbox.origin = DoubleVector(plotSize.x - toolbox.size.x, 0.0)

        eventsManager.register(toolbox)

        toolboxLayer = SvgGElement().also { decorationLayer.children().add(it) }
        toolboxLayer.children().add(toolbox.svgRoot)
    }

    fun onViewReset(function: () -> Unit) {
        onViewResetHandler = function
    }

    fun onViewZoomIn(function: (DoubleVector) -> Unit) {
        onViewZoomInHandler = function
    }

    fun onViewZoomOut(function: (DoubleVector) -> Unit) {
        onViewZoomOutHandler = function
    }

    fun onViewPanning(function: (DoubleVector) -> Unit) {
        onViewPanningHandler = function
    }

    fun onViewZoomArea(function: (DoubleRectangle) -> Unit) {
        onViewZoomAreaHandler = function
    }

    private fun rectContent(color: Color) = SvgRectElement().apply {
        fillColor().set(color)
    }

    var visible: Boolean = false
        set(value) {
            when {
                value == field -> return
                value == true -> showPanel().also { field = value }
                value == false -> hidePanel().also { field = value }
            }
        }

    private fun hidePanel() {

    }

    private fun showPanel() {

    }

    override fun dispose() {
        onViewResetHandler = null
        onViewZoomAreaHandler = null
        onViewZoomInHandler = null
        onViewZoomOutHandler = null
        onViewPanningHandler = null
        tool?.dispose()
    }


    private abstract class Tool: Disposable {

    }

    private inner class PanTool(
        private val decorationLayer: SvgNode,
        eventsManager: EventsManager
    ) : Tool() {
        private var startPos: DoubleVector? = null
        private val regs = CompositeRegistration()

        init {
            regs.add(eventsManager.onMouseEvent(MOUSE_RELEASED) { _, _ -> startPos = null })

            regs.add(eventsManager.onMouseEvent(MOUSE_DRAGGED) { _, event ->
                val delta = startPos?.subtract(event.location.toDoubleVector()) ?: ZERO
                startPos = event.location.toDoubleVector()

                if (delta != ZERO) {
                    onViewPanningHandler?.invoke(delta)
                }
            })
        }

        override fun dispose() = regs.dispose()
    }

    private inner class ZoomTool(
        private val decorationLayer: SvgNode,
        eventsManager: EventsManager
    ) : Tool() {
        private val regs = CompositeRegistration()
        private var startPos: DoubleVector? = null
        private val drawing get() = startPos != null
        private val rect = SvgRectElement().apply {
            strokeColor().set(Color.GRAY)
            fillColor().set(Color.TRANSPARENT)
            strokeWidth().set(2.0)
            x().set(0.0)
            y().set(0.0)
            width().set(0.0)
            height().set(0.0)
        }

        init {
            regs.add(eventsManager.onMouseEvent(MOUSE_DOUBLE_CLICKED) { _, event ->
                onViewZoomInHandler?.invoke(event.location.toDoubleVector())
            })

            regs.add(eventsManager.onMouseEvent(MOUSE_RELEASED) { _, event ->
                if (drawing) {
                    calcRect(event).let { onViewZoomAreaHandler?.invoke(it) }
                    startPos = null
                    decorationLayer.children().remove(rect)
                    rect.height().set(0.0)
                    rect.width().set(0.0)
                }
            })

            regs.add(eventsManager.onMouseEvent(MOUSE_DRAGGED) { _, event ->
                if (!drawing) {
                    startPos = event.location.toDoubleVector()
                    decorationLayer.children().add(rect)
                } else {
                    if (drawing) {
                        calcRect(event).let {
                            rect.x().set(it.left)
                            rect.y().set(it.top)
                            rect.width().set(it.width)
                            rect.height().set(it.height)
                        }
                    }
                }
            })
        }

        private fun calcRect(event: MouseEvent): DoubleRectangle {
            val left = min(startPos!!.x, event.x.toDouble())
            val top = min(startPos!!.y, event.y.toDouble())

            return DoubleRectangle(
                x = left,
                y = top,
                w = max(startPos!!.x, event.x.toDouble()) - left,
                h = max(startPos!!.y, event.y.toDouble()) - top
            )
        }

        override fun dispose() = regs.dispose()
    }
}
