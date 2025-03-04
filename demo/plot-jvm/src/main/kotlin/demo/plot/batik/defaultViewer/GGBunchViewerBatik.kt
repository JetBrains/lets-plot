/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.batik.defaultViewer

import demo.plot.common.model.plotConfig.GGBunch
import org.jetbrains.letsPlot.batik.plot.component.PlotViewerWindowBatik

fun main() {
    with(GGBunch()) {
        val plotSpecList = plotSpecList()
        PlotViewerWindowBatik(
            "ggbunch()",
            rawSpec = plotSpecList.first(),
//            windowSize = Dimension(400, 300)
        ).open()
    }
}
