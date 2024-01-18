/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.batik.plotConfig

import demo.common.batik.demoUtils.PlotSpecsDemoWindowBatik
import demo.plot.common.model.plotConfig.Issue_facet_free_x_barchart_955

fun main() {
    with(Issue_facet_free_x_barchart_955()) {
        PlotSpecsDemoWindowBatik(
            "Issue_facet_free_x_boxplot_955",
            plotSpecList()
        ).open()
    }
}
