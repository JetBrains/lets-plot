/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.jfx.plotConfig

import demo.plot.common.model.plotConfig.LongTooltipText
import demo.common.jfx.demoUtils.PlotSpecsDemoWindowJfx
import java.awt.Dimension

fun main() {
    with(LongTooltipText()) {
        PlotSpecsDemoWindowJfx(
            "Long text in tooltip",
            plotSpecList(),
            plotSize = Dimension(500, 700)
        ).open()
    }
}