/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.batik.plotConfig

import demo.common.batik.demoUtils.PlotSpecsDemoWindowBatik
import demo.plot.common.model.plotConfig.Issue_line_50K_932

fun main() {
    with(Issue_line_50K_932()) {
        PlotSpecsDemoWindowBatik(
            "Line 50K points",
            plotSpecList()
        ).open()
    }
}