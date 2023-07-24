/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.plotConfig

import demo.plot.common.model.plotConfig.SpecialSymbols
import demo.common.batik.demoUtils.PlotSpecsDemoWindowBatik

fun main() {
    with (SpecialSymbols()) {
        PlotSpecsDemoWindowBatik(
            "Special symbols",
            listOf(plotSpec())
        ).open()
    }
}
