/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.batik.plotConfig

import demo.plot.common.model.plotConfig.ScaleLabelJustification
import demo.common.utils.batik.PlotSpecsDemoWindowBatik

fun main() {
    with(ScaleLabelJustification()) {
        PlotSpecsDemoWindowBatik(
            "Tick label justification",
            plotSpecList()
        ).open()
    }
}