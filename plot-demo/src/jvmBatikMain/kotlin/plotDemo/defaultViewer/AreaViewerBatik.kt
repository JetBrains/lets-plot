/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.defaultViewer

import jetbrains.datalore.plotDemo.model.plotConfig.Area
import org.jetbrains.letsPlot.platf.batik.plot.component.PlotViewerWindowBatik

@Suppress("UNUSED_PARAMETER")
fun main(args: Array<String>) {
    with(Area()) {
        PlotViewerWindowBatik(
            "Area plot",
            null,
            plotSpecList().first(),
//                    Dimension(900, 700),
            preserveAspectRatio = false
        ).open()
    }
}
