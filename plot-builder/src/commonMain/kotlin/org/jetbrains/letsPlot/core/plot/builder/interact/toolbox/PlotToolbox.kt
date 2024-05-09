/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.interact.toolbox

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.registration.CompositeRegistration
import org.jetbrains.letsPlot.commons.registration.Disposable
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.interact.event.ToolEventDispatcher
import org.jetbrains.letsPlot.core.interact.event.ToolInteractionSpec
import org.jetbrains.letsPlot.core.plot.builder.PlotInteractor
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgGElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgRectElement

internal class PlotToolbox(
    private val interactor: PlotInteractor,
    private val toolEventDispatcher: ToolEventDispatcher,
) : Disposable {
    private val toolboxLayer: SvgGElement
    private val panTool = Tool("my-pan", "Pan", ToolInteractionSpec.DRAG_PAN, toolEventDispatcher)
    private val zoomTool = Tool("my-zoom", "Zoom", ToolInteractionSpec.BOX_ZOOM, toolEventDispatcher)
    private val wheelZoomTool = Tool("wheel-zoom", "Wheel Zoom", ToolInteractionSpec.WHEEL_ZOOM, toolEventDispatcher)

    init {
        val toolbox = ToolboxControl(
            listOf(
                ToggleButtonControl(
                    rectContent(Color.LIGHT_GREEN),
                    rectContent(Color.GREEN),
                ).apply {
                    onToggleClick(panTool::switch)
                },
                ToggleButtonControl(
                    rectContent(Color.LIGHT_BLUE),
                    rectContent(Color.BLUE),
                ).apply {
                    onToggleClick(zoomTool::switch)
                },
                ToggleButtonControl(
                    rectContent(Color.LIGHT_PINK),
                    rectContent(Color.PINK),
                ).apply {
                    onToggleClick(wheelZoomTool::switch)
                },
                ToggleButtonControl(
                    rectContent(Color.LIGHT_GRAY),
                    rectContent(Color.GRAY),
                ).apply {
                    onClick {
                        println("Reset View.")
                    }
                }
            ))

        interactor.eventsManager.register(toolbox)

        toolbox.origin = DoubleVector(interactor.plotSize.x - toolbox.size.x, 0.0)
        toolboxLayer = SvgGElement().also { interactor.decorationLayer.children().add(it) }
        toolboxLayer.children().add(toolbox.svgRoot)
    }

    private fun rectContent(color: Color) = SvgRectElement().apply {
        fillColor().set(color)
    }

    override fun dispose() {
    }

    private open class Tool(
        val name: String,
        val label: String,
        val interaction: String,
        private val toolEventDispatcher: ToolEventDispatcher,
    ) : Disposable {
        val interactionSpec = mapOf(
            ToolInteractionSpec.NAME to interaction
        )
        var active: Boolean = false

        fun switch(newState: Boolean) {
            if (active == newState) return
            active = newState
            if (active) {
                toolEventDispatcher.activateInteraction(origin = name, interactionSpec = interactionSpec)
            } else {
                toolEventDispatcher.deactivateInteraction(origin = name, interactionName = interaction)
            }
        }

        protected val regs = CompositeRegistration()
        override fun dispose() {
            println("Tool dispose.")
            regs.dispose()
        }
    }
}
