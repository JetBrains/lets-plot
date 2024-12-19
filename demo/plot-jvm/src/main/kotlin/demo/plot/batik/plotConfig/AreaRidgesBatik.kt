/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.batik.plotConfig

import demo.common.utils.batik.PlotSpecsDemoWindowBatik
import demo.plot.common.model.plotConfig.AreaRidges

fun main() {
    with(AreaRidges()) {
        PlotSpecsDemoWindowBatik(
            "Area ridges plot",
            plotSpecList()
        ).open()
    }
}