/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.plotConfig

import jetbrains.datalore.base.event.MouseEventSpec.MOUSE_LEFT
import jetbrains.datalore.base.event.MouseEventSpec.MOUSE_MOVED
import jetbrains.datalore.base.event.awt.AwtEventUtil
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.observable.property.ValueProperty
import jetbrains.datalore.plot.DemoAndTest
import jetbrains.datalore.plot.builder.PlotContainer
import jetbrains.datalore.vis.demoUtils.swing.SwingDemoFactory
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*

object PlotConfigDemoUtil {
    fun show(
        title: String,
        plotSpecList: List<MutableMap<String, Any>>,
        factory: SwingDemoFactory,
        plotSize: DoubleVector
    ) {
        factory.createDemoFrame(title).show {
            val panel = this
            panel.removeAll()
            panel.background = Color.WHITE
            panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)
            panel.add(Box.createRigidArea(Dimension(50, 0)))

            addPlots(panel, plotSpecList, factory, plotSize)

            panel.add(Box.createRigidArea(Dimension(0, 5)))
        }
    }

    private fun addPlots(
        panel: JPanel,
        plotSpecList: List<MutableMap<String, Any>>,
        factory: SwingDemoFactory,
        plotSize: DoubleVector
    ) {
        for (plotSpec in plotSpecList) {
            val component = createPlotComponent(
                plotSpec,
                factory,
                plotSize
            )

            component.border = BorderFactory.createLineBorder(Color.ORANGE, 1)

            component.minimumSize = Dimension(plotSize.x.toInt(), plotSize.y.toInt())
            component.maximumSize = Dimension(plotSize.x.toInt(), plotSize.y.toInt())
            component.alignmentX = Component.LEFT_ALIGNMENT

            panel.add(Box.createRigidArea(Dimension(0, 5)))
            panel.add(component)
        }
    }

    private fun createPlotComponent(
        plotSpec: MutableMap<String, Any>,
        factory: SwingDemoFactory,
        plotSize: DoubleVector
    ): JComponent {
        val plot = DemoAndTest.createPlot(plotSpec, false)
        val plotContainer = PlotContainer(plot, ValueProperty(plotSize))
        plotContainer.ensureContentBuilt()

        val component = factory.createSvgComponent(plotContainer.svg)

        // Bind mouse events
        val plotEdt = factory.createPlotEdtExecutor()
        component.addMouseListener(object : MouseAdapter() {
            override fun mouseExited(e: MouseEvent) {
                super.mouseExited(e)
                plotEdt {
                    plotContainer.mouseEventPeer.dispatch(MOUSE_LEFT, AwtEventUtil.translate(e))
                }
            }
        })
        component.addMouseMotionListener(object : MouseAdapter() {
            override fun mouseMoved(e: MouseEvent) {
                super.mouseMoved(e)
                plotEdt {
                    plotContainer.mouseEventPeer.dispatch(MOUSE_MOVED, AwtEventUtil.translate(e))
                }
            }
        })

        return component;
    }

}