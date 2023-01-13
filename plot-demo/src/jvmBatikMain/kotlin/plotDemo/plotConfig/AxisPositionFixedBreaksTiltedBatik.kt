/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.plotConfig

import jetbrains.datalore.plotDemo.model.plotConfig.AxisPositionFixedBreaksTilted
import jetbrains.datalore.vis.demoUtils.PlotSpecsDemoWindowBatik

fun main() {
    with(AxisPositionFixedBreaksTilted()) {
        PlotSpecsDemoWindowBatik(
            "Axis Position: RESIZE to see tilted labs. ",
            plotSpecList()
        ).open()
    }
}
