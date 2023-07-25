/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.batik.plotConfig

import demo.plot.common.model.plotConfig.MultiLineTooltip
import demo.common.batik.demoUtils.PlotSpecsDemoWindowBatik

fun main() {
    with(MultiLineTooltip()) {
        PlotSpecsDemoWindowBatik(
            "Multi-line tooltips plot",
            plotSpecList(),
            2
        ).open()
    }
}