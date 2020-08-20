/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.plotConfig

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plotDemo.model.plotConfig.Area
import jetbrains.datalore.plotDemo.model.plotConfig.MercatorProjection
import jetbrains.datalore.vis.demoUtils.BatikMapperDemoFactory

object MercatorProjectionBatik {
    @JvmStatic
    fun main(args: Array<String>) {
        with(MercatorProjection()) {
            @Suppress("UNCHECKED_CAST")
            val plotSpecList = plotSpecList() as List<MutableMap<String, Any>>
            PlotConfigDemoUtil.show(
                "Mercator projection",
                plotSpecList,
                BatikMapperDemoFactory(),
                DoubleVector(300.0, 300.0)
            )
        }
    }
}
