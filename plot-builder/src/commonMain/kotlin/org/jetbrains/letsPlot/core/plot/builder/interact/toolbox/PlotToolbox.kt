/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.interact.toolbox

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.registration.CompositeRegistration
import org.jetbrains.letsPlot.commons.registration.Disposable
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.interact.DrawRectFeedback
import org.jetbrains.letsPlot.core.interact.PanGeomFeedback
import org.jetbrains.letsPlot.core.plot.builder.PlotInteractor
import org.jetbrains.letsPlot.core.plot.builder.PlotTile
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgGElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgRectElement

internal class PlotToolbox(
    private val interactor: PlotInteractor
) : Disposable {
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
                            true -> PanTool(interactor)
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
                            true -> ZoomTool(interactor)
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
                        tool = null
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
        tool = null
    }


    private open class Tool : Disposable {
        protected val regs = CompositeRegistration()
        override fun dispose() {
            println("Tool dispose.")
            regs.dispose()
        }
    }

    private inner class PanTool(
        interactor: PlotInteractor
    ) : Tool() {
        init {
            regs.add(
                interactor.startToolFeedback(PanGeomFeedback(
                    onStarted = { coord, target ->
                        (target.tile as PlotTile).pan(coord)
                    },
                    onCompleted = { _, target ->
                        (target.tile as PlotTile).pan(DoubleVector.ZERO)
                    },
                    onDragged = { coord, target ->
                        (target.tile as PlotTile).pan(coord)
                    }
                ))
            )
        }
    }

    private inner class ZoomTool(
        interactor: PlotInteractor
    ) : Tool() {
        init {
            regs.add(
                interactor.startToolFeedback(DrawRectFeedback(
                    onCompleted = { (r, target) ->
                        // translate to "geom" space.
                        val translated = r.subtract(target.geomBounds.origin)
                        println("Zoom tool: apply: $translated")
                        target.zoom(translated)
                    }
                ))
            )
        }
    }
}
