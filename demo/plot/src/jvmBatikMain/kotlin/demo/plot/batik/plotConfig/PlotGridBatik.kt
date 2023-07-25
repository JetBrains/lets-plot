/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.batik.plotConfig

import demo.plot.common.model.plotConfig.PlotGrid
import demo.common.batik.demoUtils.PlotSpecsDemoWindowBatik

fun main() {
    with(PlotGrid()) {
        PlotSpecsDemoWindowBatik(
            "Plot Grid (Batik)",
            plotSpecList(),
            maxCol = 2
        ).open()
    }
}
