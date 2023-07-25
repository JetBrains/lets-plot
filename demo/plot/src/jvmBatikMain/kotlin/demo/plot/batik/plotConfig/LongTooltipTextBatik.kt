/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.batik.plotConfig

import demo.plot.common.model.plotConfig.LongTooltipText
import demo.common.batik.demoUtils.PlotSpecsDemoWindowBatik
import java.awt.Dimension

fun main() {
    with(LongTooltipText()) {
        PlotSpecsDemoWindowBatik(
            "Long text in tooltip",
            plotSpecList(),
            plotSize = Dimension(500, 700)
        ).open()
    }
}
