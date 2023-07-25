/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.batik.plotConfig

import demo.plot.common.model.plotConfig.Issue_facet_ordering_boxplot_746
import demo.common.batik.demoUtils.PlotSpecsDemoWindowBatik

fun main() {
    with(Issue_facet_ordering_boxplot_746()) {
        PlotSpecsDemoWindowBatik(
            "Issue_facet_ordering_boxplot_746",
            plotSpecList()
        ).open()
    }
}
