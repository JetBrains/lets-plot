/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.swing.defaultViewer

import demo.plot.common.model.plotConfig.Area
import org.jetbrains.letsPlot.awt.plot.util.SimplePlotViewerWindow

fun main(args: Array<String>) {
    with(Area()) {
        SimplePlotViewerWindow(
            "Area plot",
//            windowSize = Dimension(640, 480),
            windowSize = null,
            rawSpec = plotSpecList().first(),
            preserveAspectRatio = true
        ).open()
    }
}
