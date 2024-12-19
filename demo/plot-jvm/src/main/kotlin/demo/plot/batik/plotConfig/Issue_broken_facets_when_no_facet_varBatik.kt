/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.batik.plotConfig

import demo.common.utils.batik.PlotSpecsDemoWindowBatik
import demo.plot.common.model.plotConfig.Issue_broken_facets_when_no_facet_var

fun main() {
    with(Issue_broken_facets_when_no_facet_var()) {
        PlotSpecsDemoWindowBatik(
            "Issue_broken_facets_when_no_facet_var",
            plotSpecList()
        ).open()
    }
}
