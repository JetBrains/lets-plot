/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.plotConfig

import jetbrains.datalore.plotDemo.model.plotConfig.CoordLim
import jetbrains.datalore.plotDemo.model.plotConfig.LogScaleCoordLim
import jetbrains.datalore.vis.demoUtils.PlotSpecsDemoWindowBatik

fun main() {
    with(LogScaleCoordLim()) {
        PlotSpecsDemoWindowBatik(
            "coord y limits, log10 scale",
            plotSpecList()
        ).open()
    }
}