/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.swing.plotConfig

import demo.common.utils.swing.PlotSpecsDemoWindowSwing
//import demo.common.utils.swingCanvas.PlotSpecsDemoWindowSwingCanvas
import demo.plot.common.model.plotConfig.BarPlot

fun main() {
    with(BarPlot()) {
        PlotSpecsDemoWindowSwing(
            "Bar plot",
            plotSpecList()
        ).open()
    }
}
