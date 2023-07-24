/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.plotConfig

import demo.plot.common.model.plotConfig.FacetWrapDirVDemo
import demo.common.batik.demoUtils.PlotSpecsDemoWindowBatik
import java.awt.Dimension

fun main() {
    with(FacetWrapDirVDemo()) {
        PlotSpecsDemoWindowBatik(
            "Facet wrap, dir='v'",
            plotSpecList(),
            2,
            Dimension(600, 600)
        ).open()
    }
}
