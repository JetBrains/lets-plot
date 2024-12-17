/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.batik.plotConfig

import demo.common.util.demoUtils.batik.PlotSpecsDemoWindowBatik
import demo.plot.common.model.plotConfig.GGGridShareXY

fun main() {
    with(GGGridShareXY()) {
        PlotSpecsDemoWindowBatik(
            "Plot Grid with Shared Axis (Batik)",
            plotSpecList(),
            maxCol = 2
        ).open()
    }
}
