/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.plotContainer

import jetbrains.datalore.plotDemo.model.plotContainer.BarPlotResizeDemo
import jetbrains.datalore.vis.swing.BatikMapperDemoFactory

object BarPlotResizeDiscreteXDemoBatik {
    @JvmStatic
    fun main(args: Array<String>) {
        PlotResizeDemoUtil.show(BarPlotResizeDemo.discreteX(), BatikMapperDemoFactory())
    }
}