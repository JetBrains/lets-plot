/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.demo

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.livemap.jvmPackage.MonolithicAwtLM
import jetbrains.datalore.plotDemo.plotConfig.PlotConfigDemoUtil
import jetbrains.datalore.vis.demoUtils.swing.SwingDemoFactory
import java.awt.Dimension
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JComponent

object LiveMapPlotConfigDemoUtil {
    fun show(
        title: String,
        plotSpecList: List<MutableMap<String, Any>>,
        factory: SwingDemoFactory,
        plotSize: DoubleVector?
    ) {
        fun rawSpecPlotBuilder(plotSpec: MutableMap<String, Any>): JComponent {
            return MonolithicAwtLM.buildPlotFromRawSpecs(
                plotSpec,
                plotSize,
                null,
                factory::createSvgComponent,
                factory.createPlotEdtExecutor()
            ) {
                for (s in it) {
                    println("DEMO PLOT INFO: $s")
                }
            }
        }

        create(title, plotSpecList, factory, plotSize, ::rawSpecPlotBuilder, MonolithicAwtLM::mapsToCanvas)
    }

    private fun create(
        title: String,
        plotSpecList: List<MutableMap<String, Any>>,
        factory: SwingDemoFactory,
        plotSize: DoubleVector?,
        rawSpecPlotBuilder: (plotSpec: MutableMap<String, Any>) -> JComponent,
        runAfterShow: () -> Unit
    ) {
        factory.createDemoFrame(title).show {
            val panel = this
            panel.removeAll()

            panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)
            panel.add(Box.createRigidArea(Dimension(50, 0)))

            PlotConfigDemoUtil.addPlots(panel, plotSpecList, plotSize, rawSpecPlotBuilder)

            panel.add(Box.createRigidArea(Dimension(0, 5)))

            runAfterShow()
        }
    }
}