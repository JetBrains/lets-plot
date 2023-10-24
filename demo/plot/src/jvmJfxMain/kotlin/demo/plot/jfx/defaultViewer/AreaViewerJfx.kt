/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.jfx.defaultViewer

import demo.plot.common.model.plotConfig.Area
import org.jetbrains.letsPlot.jfx.plot.component.PlotViewerWindowJfx

fun main(args: Array<String>) {
    with(Area()) {
        PlotViewerWindowJfx(
            "Area plot",
            null,
            plotSpecList().first(),
//                    Dimension(900, 700),
            preserveAspectRatio = false
        ).open()
    }
}
