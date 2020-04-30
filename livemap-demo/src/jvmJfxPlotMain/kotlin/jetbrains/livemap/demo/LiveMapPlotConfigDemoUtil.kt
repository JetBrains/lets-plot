/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.demo

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.livemap.jvmPackage.MonolithicAwtLM
import jetbrains.datalore.plotDemo.plotConfig.PlotConfigDemoUtil.create
import jetbrains.datalore.vis.demoUtils.swing.SwingDemoFactory
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
                factory::createSvgComponent,
                factory.createPlotEdtExecutor()
            ) {
                for (s in it) {
                    println("DEMO PLOT INFO: $s")
                }
            }
        }

        create(title,plotSpecList, factory, plotSize, ::rawSpecPlotBuilder)
    }
}