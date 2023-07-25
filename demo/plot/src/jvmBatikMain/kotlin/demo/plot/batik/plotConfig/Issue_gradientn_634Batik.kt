/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.batik.plotConfig

import demo.plot.common.model.plotConfig.Issue_gradientn_634
import demo.common.batik.demoUtils.PlotSpecsDemoWindowBatik

fun main() {
    with(Issue_gradientn_634()) {
        PlotSpecsDemoWindowBatik(
            "Issue_gradientn_634",
            plotSpecList()
        ).open()
    }
}
