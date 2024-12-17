/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.batik.plotConfig

import demo.common.util.demoUtils.batik.PlotSpecsDemoWindowBatik
import demo.plot.common.model.plotConfig.AxisPositionFixedBreaks90Deg

fun main() {
    with(AxisPositionFixedBreaks90Deg()) {
        PlotSpecsDemoWindowBatik(
            "Axis Position",
            plotSpecList()
        ).open()
    }
}
