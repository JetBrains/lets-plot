/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.batik.plotConfig

import demo.common.util.demoUtils.batik.PlotSpecsDemoWindowBatik
import demo.plot.common.model.plotConfig.FacetGridDemo
import java.awt.Dimension

fun main() {
    with(FacetGridDemo()) {
        PlotSpecsDemoWindowBatik(
            "Facet grid",
            plotSpecList(),
            2,
            Dimension(600, 400)
        ).open()
    }
}
