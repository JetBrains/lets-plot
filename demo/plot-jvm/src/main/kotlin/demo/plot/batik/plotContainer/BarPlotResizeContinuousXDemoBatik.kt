/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.batik.plotContainer

import demo.common.util.demoUtils.batik.PlotResizableDemoWindowBatik
import demo.plot.shared.model.plotContainer.BarPlotResizeDemo

fun main() {
    with(BarPlotResizeDemo.continuousX()) {
        PlotResizableDemoWindowBatik(
            "Bar plot (X-continuous)",
            plotAssembler = createPlotAssembler()
        ).open()
    }
}