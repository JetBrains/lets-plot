/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.plotConfig

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plotDemo.model.plotConfig.HVLine
import jetbrains.datalore.vis.demoUtils.BatikMapperDemoFactory

object HVLineBatik {
    @JvmStatic
    fun main(args: Array<String>) {
        with(HVLine() ) {
            @Suppress("UNCHECKED_CAST")
            val plotSpecList = plotSpecList() as List<MutableMap<String, Any>>
            PlotConfigDemoUtil.show(
                "Rect tooltips plot",
                plotSpecList,
                BatikMapperDemoFactory(),
                DoubleVector(600.0, 400.0)
            )
        }
    }
}