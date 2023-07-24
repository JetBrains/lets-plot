/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.plotConfig

import demo.plot.common.model.plotConfig.TooltipAnchor
import demo.common.batik.demoUtils.PlotSpecsDemoWindowBatik

fun main() {
    with(TooltipAnchor()) {
        PlotSpecsDemoWindowBatik(
            "Tooltip Anchor",
            plotSpecList()
        ).open()
    }
}
