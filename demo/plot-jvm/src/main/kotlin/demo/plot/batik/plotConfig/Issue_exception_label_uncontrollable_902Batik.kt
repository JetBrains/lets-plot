/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.batik.plotConfig

import demo.common.utils.batik.PlotSpecsDemoWindowBatik
import demo.plot.common.model.plotConfig.Issue_exception_label_uncontrollable_902

/**
 * https://github.com/JetBrains/lets-plot/issues/902
 */
fun main() {
    with(Issue_exception_label_uncontrollable_902()) {
        PlotSpecsDemoWindowBatik(
            "Issue_exception_label_uncontrollable_902",
            plotSpecList(),
            maxCol = 2
        ).open()
    }
}
