/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.batik.plotConfig

import demo.common.batik.demoUtils.PlotSpecsDemoWindowBatik
import demo.plot.common.model.plotConfig.AsDiscrete

fun main() {
    with(AsDiscrete()) {
        PlotSpecsDemoWindowBatik(
            "as_discrete",
            plotSpecList()
        ).open()
    }
}
