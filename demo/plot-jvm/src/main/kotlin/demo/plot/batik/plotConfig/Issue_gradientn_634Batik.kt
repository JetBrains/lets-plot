/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.batik.plotConfig

import demo.common.utils.batik.PlotSpecsDemoWindowBatik
import demo.plot.common.model.plotConfig.Issue_gradientn_634

fun main() {
    with(Issue_gradientn_634()) {
        PlotSpecsDemoWindowBatik(
            "Issue_gradientn_634",
            plotSpecList()
        ).open()
    }
}