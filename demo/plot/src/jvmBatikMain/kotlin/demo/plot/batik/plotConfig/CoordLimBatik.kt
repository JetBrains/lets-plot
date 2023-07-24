/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.plotConfig

import demo.plot.common.model.plotConfig.CoordLim
import demo.common.batik.demoUtils.PlotSpecsDemoWindowBatik

fun main() {
    with(CoordLim()) {
        PlotSpecsDemoWindowBatik(
            "coord x/y limits",
            plotSpecList()
        ).open()
    }
}