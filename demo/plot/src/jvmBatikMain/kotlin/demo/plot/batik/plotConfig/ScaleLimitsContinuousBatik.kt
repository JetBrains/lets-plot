/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.batik.plotConfig

import demo.plot.common.model.plotConfig.ScaleLimitsContinuous
import demo.common.batik.demoUtils.PlotSpecsDemoWindowBatik
import java.awt.Dimension

fun main() {
    with(ScaleLimitsContinuous()) {
        PlotSpecsDemoWindowBatik(
            "Scale limits (continuous)",
            plotSpecList(),
            2,
            Dimension(500, 200)
        ).open()
    }
}