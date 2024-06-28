/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.batik.plotConfig

import demo.common.batik.demoUtils.PlotSpecsDemoWindowBatik
import demo.plot.common.model.plotConfig.CustomLegend

fun main() {
    with(CustomLegend()) {
        PlotSpecsDemoWindowBatik(
            "Custom legend",
            plotSpecList()
        ).open()
    }
}
