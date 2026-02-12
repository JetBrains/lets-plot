/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.swing.defaultViewer

import demo.plot.common.model.plotConfig.GGBunch
import org.jetbrains.letsPlot.awt.plot.util.SimplePlotViewerWindow

fun main() {
    with(GGBunch()) {
        val plotSpecList = plotSpecList()
        SimplePlotViewerWindow(
            "ggbunch()",
            rawSpec = plotSpecList.first(),
//            windowSize = Dimension(400, 300)
        ).open()
    }
}
