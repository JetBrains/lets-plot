/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.plotContainer

import jetbrains.datalore.plot.builder.presentation.Style
import jetbrains.datalore.plotDemo.model.plotContainer.BarPlotResizeDemo
import jetbrains.datalore.vis.demoUtils.SceneMapperDemoFactory

object BarPlotResizeDiscreteXDemoJfx {
    @JvmStatic
    fun main(args: Array<String>) {
        PlotResizeDemoUtil.show(BarPlotResizeDemo.discreteX(),
            SceneMapperDemoFactory(Style.JFX_PLOT_STYLESHEET)
        )
    }
}