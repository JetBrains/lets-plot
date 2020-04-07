/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.plotConfig

import jetbrains.datalore.plotDemo.model.plotConfig.Raster
import jetbrains.datalore.vis.demoUtils.BatikMapperDemoFactory

object RasterBatik {
    @JvmStatic
    fun main(args: Array<String>) {
        with(Raster()) {
            @Suppress("UNCHECKED_CAST")
            val plotSpecList = plotSpecList() as List<MutableMap<String, Any>>
            PlotConfigDemoUtil.show(
                "raster_geom",
                plotSpecList,
                BatikMapperDemoFactory(),
                demoComponentSize
            )
        }
    }
}
