/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.plotContainer

import demo.common.batik.demoUtils.PlotResizableDemoWindowBatik
import demo.plot.shared.model.plotContainer.BarPlotResizeDemo

fun main() {
    with(BarPlotResizeDemo.continuousX()) {
        PlotResizableDemoWindowBatik(
            "Bar plot (X-continuous)",
            plotAssembler = createPlotAssembler()
        ).open()
    }
}