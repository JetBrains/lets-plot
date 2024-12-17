/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.batik.plotConfig

import demo.common.util.demoUtils.batik.PlotSpecsDemoWindowBatik
import demo.plot.shared.model.scale.TimeScaleX

fun main() {
    with(TimeScaleX()) {
        PlotSpecsDemoWindowBatik(
            "Time scale",
            plotSpecList()
        ).open()
    }
}
