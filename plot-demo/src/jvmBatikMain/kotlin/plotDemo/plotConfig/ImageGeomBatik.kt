/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.plotConfig

import jetbrains.datalore.plotDemo.model.plotConfig.BarPlot
import jetbrains.datalore.plotDemo.model.plotConfig.ImageGeom
import jetbrains.datalore.vis.demoUtils.BatikMapperDemoFactory

object ImageGeomBatik {
    @JvmStatic
    fun main(args: Array<String>) {
        with(ImageGeom()) {
            @Suppress("UNCHECKED_CAST")
            val plotSpecList = plotSpecList() as List<MutableMap<String, Any>>
            PlotConfigDemoUtil.show(
                "image_geom",
                plotSpecList,
                BatikMapperDemoFactory(),
                demoComponentSize
            )
        }
    }
}
