/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.plotConfig

import jetbrains.datalore.plotDemo.model.plotConfig.Issue_OOM_105
import jetbrains.datalore.plotDemo.model.plotConfig.Issue_slow_tooltip
import jetbrains.datalore.vis.demoUtils.PlotSpecsDemoWindowBatik

fun main() {
    with(Issue_slow_tooltip()) {
        PlotSpecsDemoWindowBatik(
            "Issue_slow_tooltip",
            plotSpecList()
        ).open()
    }
}
