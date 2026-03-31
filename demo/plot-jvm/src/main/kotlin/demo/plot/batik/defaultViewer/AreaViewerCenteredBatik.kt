/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.batik.defaultViewer

import demo.plot.common.model.plotConfig.Area
import org.jetbrains.letsPlot.batik.plot.component.PlotViewerWindowBatik
import java.awt.Dimension

@Suppress("UNUSED_PARAMETER")
fun main(args: Array<String>) {
    with(Area()) {
        PlotViewerWindowBatik(
            "Area plot",
//            windowSize = Dimension(640, 480),
            windowSize = null,
            rawSpec = plotSpecList().first(),
            preserveAspectRatio = true
        ).open()
    }
}
