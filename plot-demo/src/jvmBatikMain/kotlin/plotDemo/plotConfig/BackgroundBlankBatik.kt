/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.plotConfig

import jetbrains.datalore.plotDemo.model.plotConfig.BackgroundBlank
import jetbrains.datalore.vis.demoUtils.PlotSpecsDemoWindowBatik
import java.awt.Color

fun main() {
    with(BackgroundBlank()) {
        PlotSpecsDemoWindowBatik(
            "Plot background - blank (in a pink window)",
            plotSpecList(),
            background = Color.PINK
        ).open()
    }
}
