/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.batik.plotConfig

import demo.common.util.demoUtils.batik.PlotSpecsDemoWindowBatik
import demo.plot.common.model.plotConfig.BackgroundBlank
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
