/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.batik.plotConfig

import demo.plot.common.model.plotConfig.AxisPositionFixedBreaks90Deg
import demo.common.batik.demoUtils.PlotSpecsDemoWindowBatik

fun main() {
    with(AxisPositionFixedBreaks90Deg()) {
        PlotSpecsDemoWindowBatik(
            "Axis Position",
            plotSpecList()
        ).open()
    }
}
