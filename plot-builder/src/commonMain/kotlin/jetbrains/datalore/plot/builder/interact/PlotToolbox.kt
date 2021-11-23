/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.registration.CompositeRegistration
import jetbrains.datalore.base.registration.Disposable
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.builder.interact.tool.DrawRectFeedback
import jetbrains.datalore.plot.builder.interact.tool.PanGeomFeedback
import jetbrains.datalore.plot.builder.interact.ui.ToggleButtonControl
import jetbrains.datalore.plot.builder.interact.ui.ToolboxControl
import jetbrains.datalore.vis.svg.SvgGElement
import jetbrains.datalore.vis.svg.SvgRectElement

internal class PlotToolbox(
    private val interactor: Interactor
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
                    onCompleted = {
                        println("Pan tool: apply: $it")
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
