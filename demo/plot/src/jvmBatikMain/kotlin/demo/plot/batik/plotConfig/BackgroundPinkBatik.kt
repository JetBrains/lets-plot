/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.batik.plotConfig

import demo.plot.common.model.plotConfig.BackgroundPink
import demo.common.batik.demoUtils.PlotSpecsDemoWindowBatik
import java.awt.Color

fun main() {
    with(BackgroundPink()) {
        PlotSpecsDemoWindowBatik(
            "Plot background - pink (in a pink window)",
            plotSpecList(),
            background = Color.PINK
        ).open()
    }
}
