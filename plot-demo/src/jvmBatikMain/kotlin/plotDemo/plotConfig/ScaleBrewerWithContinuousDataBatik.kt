/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.plotConfig

import jetbrains.datalore.plotDemo.model.plotConfig.ScaleBrewerWithContinuousData
import jetbrains.datalore.vis.demoUtils.PlotSpecsViewerDemoWindowBatik

fun main() {
    with(ScaleBrewerWithContinuousData()) {
        PlotSpecsViewerDemoWindowBatik(
            "Scale Brewer with discrete data",
            plotSpecList(),
            2
        ).open()
    }
}