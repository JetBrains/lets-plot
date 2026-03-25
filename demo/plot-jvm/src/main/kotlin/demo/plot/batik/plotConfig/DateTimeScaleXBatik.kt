/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.batik.plotConfig

import demo.common.utils.batik.PlotSpecsDemoWindowBatik
import demo.plot.shared.model.scale.DateTimeScaleX

fun main() {
    with(DateTimeScaleX()) {
        PlotSpecsDemoWindowBatik(
            "Datetime scale",
            plotSpecList()
        ).open()
    }
}
