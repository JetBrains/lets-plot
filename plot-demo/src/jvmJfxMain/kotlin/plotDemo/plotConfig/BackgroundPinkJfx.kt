/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.plotConfig

import jetbrains.datalore.plotDemo.model.plotConfig.BackgroundPink
import jetbrains.datalore.vis.demoUtils.PlotSpecsDemoWindowJfx
import java.awt.Color

fun main() {
    with(BackgroundPink()) {
        PlotSpecsDemoWindowJfx(
            "Plot background - pink (in a pink window)",
            plotSpecList(),
            background = Color.PINK
        ).open()
    }
}
