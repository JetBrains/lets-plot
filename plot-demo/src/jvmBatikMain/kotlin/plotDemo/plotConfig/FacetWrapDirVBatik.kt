/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.plotConfig

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plotDemo.model.plotConfig.FacetWrapDirVDemo
import jetbrains.datalore.vis.demoUtils.BatikMapperDemoFactory

object FacetWrapDirVBatik {
    @JvmStatic
    fun main(args: Array<String>) {
        with(FacetWrapDirVDemo()) {
            @Suppress("UNCHECKED_CAST")
            val plotSpecList = plotSpecList() as List<MutableMap<String, Any>>
            PlotConfigDemoUtil.show(
                "Facet wrap",
                plotSpecList,
                BatikMapperDemoFactory(),
                DoubleVector(600.0, 600.0)
            )
        }
    }
}
