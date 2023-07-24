/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.plotConfig

import demo.plot.common.model.plotConfig.Issue_OOM_105
import demo.common.batik.demoUtils.PlotSpecsDemoWindowBatik

fun main() {
    with(Issue_OOM_105()) {
        PlotSpecsDemoWindowBatik(
            "Issue_OOM_105",
            plotSpecList()
        ).open()
    }
}
