/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.batik.plotContainer

import demo.common.utils.batik.PlotResizableDemoWindowBatik
import demo.plot.shared.model.plotContainer.BarPlotResizeDemo

fun main() {
    with(BarPlotResizeDemo.discreteX()) {
        PlotResizableDemoWindowBatik(
            "Bar plot (X-discrete)",
            plotAssembler = createPlotAssembler()
        ).open()
    }
}