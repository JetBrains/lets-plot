/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.plotConfig

import jetbrains.datalore.plotDemo.model.plotConfig.PointAndLineSize
import jetbrains.datalore.vis.demoUtils.PlotSpecsDemoWindowBatik

fun main() {
    with(PointAndLineSize()) {
        PlotSpecsDemoWindowBatik(
            "Point and line size",
            plotSpecList(),
            maxCol = 2
        ).open()
    }
}
