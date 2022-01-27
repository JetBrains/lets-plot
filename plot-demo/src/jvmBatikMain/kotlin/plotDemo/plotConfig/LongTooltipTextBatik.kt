/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.plotConfig

import jetbrains.datalore.plotDemo.model.plotConfig.LongTooltipText
import jetbrains.datalore.vis.demoUtils.PlotSpecsDemoWindowBatik
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
