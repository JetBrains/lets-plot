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
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgGElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgRectElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextElement

internal class PlotToolbox(
    private val interactor: PlotInteractor,
    toolEventDispatcher: ToolEventDispatcher,
) : Disposable {
    private val toolboxLayer: SvgGElement
    private val panTool = Tool("my-pan", "Pan", ToolInteractionSpec.DRAG_PAN, toolEventDispatcher)
    private val zoomTool = Tool("my-zoom", "Zoom", ToolInteractionSpec.BOX_ZOOM, toolEventDispatcher)
    private val wheelZoomTool = Tool("wheel-zoom", "Wheel Zoom", ToolInteractionSpec.WHEEL_ZOOM, toolEventDispatcher)

    init {
        val toolbox = ToolboxControl(
            listOf(
                ToggleButtonControl(
                    rectContent(Color.LIGHT_GREEN, "pan", Color.GRAY),
                    rectContent(Color.GREEN, "PAN", Color.BLACK),
                ).apply {
                    onToggleClick(panTool::switch)
                },
                ToggleButtonControl(
                    rectContent(Color.LIGHT_BLUE, "box", Color.GRAY),
                    rectContent(Color.BLUE, "BOX", Color.BLACK),
                ).apply {
                    onToggleClick(zoomTool::switch)
                },
                ToggleButtonControl(
                    rectContent(Color.LIGHT_PINK, "whl", Color.GRAY),
                    rectContent(Color.PINK, "WHL", Color.BLACK),
                ).apply {
                    onToggleClick(wheelZoomTool::switch)
                },
                ToggleButtonControl(
                    rectContent(Color.LIGHT_GRAY, "Rst", Color.GRAY),
                    rectContent(Color.GRAY, "Rst", Color.GRAY),
                ).apply {
                    onClick {
                        interactor.reset()
                    }
                }
            ))

        interactor.eventsManager.register(toolbox)

        toolbox.origin = DoubleVector(interactor.plotSize.x - toolbox.size.x, 0.0)
        toolboxLayer = SvgGElement().also { interactor.decorationLayer.children().add(it) }
        toolboxLayer.children().add(toolbox.svgRoot)
    }

    private fun rectContent(color: Color, text: String, textColor: Color): SvgElement {
        return SvgGElement().apply {
            children().add(SvgRectElement().apply {
                fillColor().set(color)
            })
            children().add(
                SvgTextElement(0.0, 16.0, text).apply {
                    fillColor().set(textColor)
                }
            )
        }
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
                toolEventDispatcher.activateInteractions(origin = name, interactionSpecList = listOf(interactionSpec))
            } else {
                toolEventDispatcher.deactivateInteractions(origin = name)
            }
        }

        protected val regs = CompositeRegistration()
        override fun dispose() {
            regs.dispose()
        }
    }
}
