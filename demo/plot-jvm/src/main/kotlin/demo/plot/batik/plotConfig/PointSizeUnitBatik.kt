/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.batik.plotConfig

import demo.common.util.demoUtils.batik.PlotSpecsDemoWindowBatik
import demo.plot.common.model.plotConfig.PointSizeUnit

fun main() {
    with(PointSizeUnit()) {
        PlotSpecsDemoWindowBatik(
            "Point with flexible size",
            plotSpecList(),
            maxCol = 2
        ).open()
    }
}