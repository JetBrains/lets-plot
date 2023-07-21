/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.defaultViewer

import jetbrains.datalore.plotDemo.model.plotConfig.GGBunch
import org.jetbrains.letsPlot.platf.batik.plot.component.PlotViewerWindowBatik

@Suppress("UNUSED_PARAMETER")
fun main(args: Array<String>) {
    with(GGBunch()) {
        @Suppress("UNCHECKED_CAST")
        val plotSpecList = plotSpecList()
        PlotViewerWindowBatik(
            "GGBunch",
            rawSpec = plotSpecList.first(),
//                windowSize = Dimension(400, 300)
        ).open()
    }
}
