/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.batik.plotConfig


import demo.plot.common.model.plotConfig.AxisPositionFixedBreaksRotated
import demo.common.batik.demoUtils.PlotSpecsDemoWindowBatik

fun main() {
    with(AxisPositionFixedBreaksRotated()) {
        PlotSpecsDemoWindowBatik(
            "Rotated axis labels",
            plotSpecList()
        ).open()
    }
}
