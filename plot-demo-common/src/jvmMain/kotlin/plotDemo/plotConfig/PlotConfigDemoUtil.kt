/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.plotConfig

import jetbrains.datalore.base.geometry.DoubleVector
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.awt.Label
import javax.swing.Box
import javax.swing.JComponent
import javax.swing.JPanel

object PlotConfigDemoUtil {
    fun addPlots(
        panel: JPanel,
        plotSpecList: List<MutableMap<String, Any>>,
        plotSize: DoubleVector?,
        rawSpecPlotBuilder: (plotSpec: MutableMap<String, Any>) -> JComponent
    ) {
        try {

            for (plotSpec in plotSpecList) {
                val component = rawSpecPlotBuilder(plotSpec)
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