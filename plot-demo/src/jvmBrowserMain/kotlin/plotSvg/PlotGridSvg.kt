/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotSvg

import jetbrains.datalore.plotDemo.model.plotConfig.Area
import jetbrains.datalore.plotDemo.model.plotConfig.PlotGrid

object PlotGridSvg {
    @JvmStatic
    fun main(args: Array<String>) {
        with(PlotGrid()) {
            @Suppress("UNCHECKED_CAST")
            (PlotSvgDemoUtil.show(
                "Plot Grid",
                plotSpecList(),
//                demoComponentSize
            ))
        }
    }
}
