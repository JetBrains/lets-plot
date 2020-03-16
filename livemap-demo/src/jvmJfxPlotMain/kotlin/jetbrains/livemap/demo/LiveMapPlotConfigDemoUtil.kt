/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.demo

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.livemap.jfxPackage.LiveMapMonolithicJfx
import jetbrains.datalore.plotDemo.plotConfig.PlotConfigDemoUtil.create
import jetbrains.datalore.vis.demoUtils.swing.SwingDemoFactory

object LiveMapPlotConfigDemoUtil {
    fun show(
        title: String,
        plotSpecList: List<MutableMap<String, Any>>,
        factory: SwingDemoFactory,
        plotSize: DoubleVector?
    ) {
        val monolithic = LiveMapMonolithicJfx(
            factory::createSvgComponent,
            factory.createPlotEdtExecutor()
        )

        create(title,plotSpecList, factory, plotSize, monolithic)
    }
}