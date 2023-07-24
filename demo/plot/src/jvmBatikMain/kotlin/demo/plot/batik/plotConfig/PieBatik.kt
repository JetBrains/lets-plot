/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.plotConfig

import demo.plot.common.model.plotConfig.Pie
import demo.common.batik.demoUtils.PlotSpecsDemoWindowBatik

fun main() {
    with(Pie()) {
        PlotSpecsDemoWindowBatik(
            "Pie chart",
            plotSpecList(),
            maxCol = 2
        ).open()
    }
}