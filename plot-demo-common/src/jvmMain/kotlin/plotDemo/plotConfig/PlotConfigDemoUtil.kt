/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.plotConfig

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.Monolithic
import jetbrains.datalore.plot.MonolithicAwt
import jetbrains.datalore.vis.demoUtils.swing.SwingDemoFactory
import java.awt.*
import javax.swing.BorderFactory
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JPanel

object PlotConfigDemoUtil {
    fun show(
        title: String,
        plotSpecList: List<MutableMap<String, Any>>,
        factory: SwingDemoFactory,
        plotSize: DoubleVector?
    ) {
        val monolithic = MonolithicAwt(
            factory::createSvgComponent,
            factory.createPlotEdtExecutor()
        )

        create(title, plotSpecList, factory, plotSize, monolithic)
    }

    fun create(
        title: String,
        plotSpecList: List<MutableMap<String, Any>>,
        factory: SwingDemoFactory,
        plotSize: DoubleVector?,
        monolithic: Monolithic
    ) {
        factory.createDemoFrame(title).show {
            val panel = this
            panel.removeAll()
            panel.background = Color.WHITE
            panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)
            panel.add(Box.createRigidArea(Dimension(50, 0)))

            addPlots(panel, plotSpecList, monolithic, plotSize)

            panel.add(Box.createRigidArea(Dimension(0, 5)))
        }
    }

    private fun addPlots(
        panel: JPanel,
        plotSpecList: List<MutableMap<String, Any>>,
        monolithic: Monolithic,
        plotSize: DoubleVector?
    ) {
        try {

            for (plotSpec in plotSpecList) {
                val component = monolithic.buildPlotFromRawSpecs(plotSpec, plotSize) {
                    for (s in it) {
                        println("DEMO PLOT INFO: $s")
                    }
                }

                component.border = BorderFactory.createLineBorder(Color.ORANGE, 1)

                if (plotSize != null) {
                    component.minimumSize = Dimension(plotSize.x.toInt(), plotSize.y.toInt())
                    component.maximumSize = Dimension(plotSize.x.toInt(), plotSize.y.toInt())
                }
                component.alignmentX = Component.LEFT_ALIGNMENT

                panel.add(Box.createRigidArea(Dimension(0, 5)))
                panel.add(component)
            }
        } catch (e: Exception) {
            panel.add(Label().apply { text = e.message; alignment = Label.CENTER; foreground = Color.RED })
        }
    }
}