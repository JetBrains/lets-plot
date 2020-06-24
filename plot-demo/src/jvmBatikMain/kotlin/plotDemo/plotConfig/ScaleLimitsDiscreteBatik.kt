/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.plotConfig

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plotDemo.model.plotConfig.ScaleLimitsDiscrete
import jetbrains.datalore.vis.demoUtils.BatikMapperDemoFactory

object ScaleLimitsDiscreteBatik {
    @JvmStatic
    fun main(args: Array<String>) {
        with(ScaleLimitsDiscrete()) {
            @Suppress("UNCHECKED_CAST")
            val plotSpecList = plotSpecList() as List<MutableMap<String, Any>>
            PlotConfigDemoUtil.show(
                "Scale limits (discrete)",
                plotSpecList,
                BatikMapperDemoFactory(),
                DoubleVector(600.0, 400.0)
            )
        }
    }
}