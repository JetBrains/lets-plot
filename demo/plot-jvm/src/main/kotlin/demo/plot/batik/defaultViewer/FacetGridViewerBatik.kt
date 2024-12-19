/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.batik.defaultViewer

import demo.plot.common.model.plotConfig.FacetGridDemo
import org.jetbrains.letsPlot.batik.plot.component.PlotViewerWindowBatik

fun main() {
    with(FacetGridDemo()) {
        @Suppress("UNCHECKED_CAST")
        val plotSpecList = plotSpecList()
        PlotViewerWindowBatik(
            "Facet grid",
            null,
            plotSpecList.last()
//                    Dimension(900, 700)
        ).open()
    }
}
