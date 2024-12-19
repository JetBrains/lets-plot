/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.batik.plotConfig

import demo.common.utils.batik.PlotSpecsDemoWindowBatik
import demo.plot.common.model.plotConfig.PositionStackFill

fun main() {
    with(PositionStackFill()) {
        PlotSpecsDemoWindowBatik(
            "Position: stack/fill",
            plotSpecList()
        ).open()
    }
}
