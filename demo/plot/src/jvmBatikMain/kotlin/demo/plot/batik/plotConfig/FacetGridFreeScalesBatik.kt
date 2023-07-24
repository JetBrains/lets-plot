/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.plotConfig

import demo.plot.common.model.plotConfig.FacetGridFreeScalesDemo
import demo.common.batik.demoUtils.PlotSpecsDemoWindowBatik
import java.awt.Dimension

fun main() {
    with(FacetGridFreeScalesDemo()) {
        PlotSpecsDemoWindowBatik(
            "Facet grid, free scales",
            plotSpecList(),
            2,
            Dimension(600, 400)
        ).open()
    }
}
