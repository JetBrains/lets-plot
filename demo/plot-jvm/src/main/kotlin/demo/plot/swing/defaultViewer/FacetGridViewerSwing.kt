/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.swing.defaultViewer

import demo.plot.common.model.plotConfig.FacetGridDemo
import org.jetbrains.letsPlot.awt.plot.util.SimplePlotViewerWindow

fun main() {
    with(FacetGridDemo()) {
        val plotSpecList = plotSpecList()
        SimplePlotViewerWindow(
            "Facet grid",
            null,
            plotSpecList.last()
//                    Dimension(900, 700)
        ).open()
    }
}
