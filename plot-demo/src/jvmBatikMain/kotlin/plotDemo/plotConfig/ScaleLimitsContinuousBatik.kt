/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.plotConfig

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plotDemo.model.plotConfig.ScaleLimitsContinuous
import jetbrains.datalore.plotDemo.model.plotConfig.ScaleLimitsDiscrete
import jetbrains.datalore.vis.demoUtils.BatikMapperDemoFactory

object ScaleLimitsContinuousBatik {
    @JvmStatic
    fun main(args: Array<String>) {
        with(ScaleLimitsContinuous()) {
            @Suppress("UNCHECKED_CAST")
            val plotSpecList = plotSpecList() as List<MutableMap<String, Any>>
            PlotConfigDemoUtil.show(
                "Scale limits (continuous)",
                plotSpecList,
                BatikMapperDemoFactory(),
                DoubleVector(500.0, 200.0)
            )
        }
    }
}