/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.batik.plotConfig

import demo.common.utils.batik.PlotSpecsDemoWindowBatik
import demo.plot.common.model.plotConfig.TooltipAnchor

fun main() {
    with(TooltipAnchor()) {
        PlotSpecsDemoWindowBatik(
            "Tooltip Anchor",
            plotSpecList()
        ).open()
    }
}
