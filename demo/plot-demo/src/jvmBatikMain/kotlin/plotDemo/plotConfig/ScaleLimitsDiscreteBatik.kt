/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.plotConfig

import jetbrains.datalore.plotDemo.model.plotConfig.ScaleLimitsDiscrete
import jetbrains.datalore.vis.demoUtils.PlotSpecsDemoWindowBatik
import java.awt.Dimension

fun main() {
    with(ScaleLimitsDiscrete()) {
        PlotSpecsDemoWindowBatik(
            "Scale limits (discrete)",
            plotSpecList(),
            2,
            Dimension(600, 200)
        ).open()
    }
}