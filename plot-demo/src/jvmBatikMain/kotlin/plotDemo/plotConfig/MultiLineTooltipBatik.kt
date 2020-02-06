/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.plotConfig

import jetbrains.datalore.plotDemo.model.plotConfig.MultiLineTooltip
import jetbrains.datalore.vis.demoUtils.BatikMapperDemoFactory

object MultiLineTooltipBatik {
    @JvmStatic
    fun main(args: Array<String>) {
        with(MultiLineTooltip() ) {
            @Suppress("UNCHECKED_CAST")
            val plotSpecList = plotSpecList() as List<MutableMap<String, Any>>
            PlotConfigDemoUtil.show(
                "Multi-line tooltips plot",
                plotSpecList,
                BatikMapperDemoFactory(),
                demoComponentSize
            )
        }
    }
}