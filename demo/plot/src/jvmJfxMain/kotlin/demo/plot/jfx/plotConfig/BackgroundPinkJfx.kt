/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.jfx.plotConfig

import demo.plot.common.model.plotConfig.BackgroundPink
import demo.common.jfx.demoUtils.PlotSpecsDemoWindowJfx
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
