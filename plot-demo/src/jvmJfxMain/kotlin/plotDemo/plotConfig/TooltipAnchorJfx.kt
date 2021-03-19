/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.plotConfig

import jetbrains.datalore.plotDemo.model.plotConfig.TooltipAnchor
import jetbrains.datalore.vis.demoUtils.PlotSpecsDemoWindowJfx

fun main() {
    with(TooltipAnchor()) {
        PlotSpecsDemoWindowJfx(
            "Tooltip Anchor",
            plotSpecList()
        ).open()
    }
}
