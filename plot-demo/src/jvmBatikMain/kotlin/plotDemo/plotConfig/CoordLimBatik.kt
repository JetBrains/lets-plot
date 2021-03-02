/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.plotConfig

import jetbrains.datalore.plotDemo.model.plotConfig.CoordLim
import jetbrains.datalore.vis.demoUtils.BatikMapperDemoFactory
import jetbrains.datalore.vis.demoUtils.PlotSpecsViewerDemoWindowBatik

object CoordLimBatik {
    @JvmStatic
    fun main(args: Array<String>) {
        with(CoordLim()) {
            PlotSpecsViewerDemoWindowBatik.show(
                "coord x/y limits",
                plotSpecList()
            )
        }
    }
}