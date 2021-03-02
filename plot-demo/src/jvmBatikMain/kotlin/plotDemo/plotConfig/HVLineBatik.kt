/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.plotConfig

import jetbrains.datalore.plotDemo.model.plotConfig.HVLine
import jetbrains.datalore.vis.demoUtils.PlotSpecsViewerDemoWindowBatik

object HVLineBatik {
    @JvmStatic
    fun main(args: Array<String>) {
        with(HVLine()) {
            PlotSpecsViewerDemoWindowBatik.show(
                "hline & vline plot",
                plotSpecList()
            )
        }
    }
}