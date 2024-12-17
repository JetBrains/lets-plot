/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.jfx.plotContainer

import demo.common.util.demoUtils.jfx.PlotResizableDemoWindowJfx
import demo.plot.shared.model.plotContainer.BarPlotResizeDemo

fun main() {
    with(BarPlotResizeDemo.continuousX()) {
        PlotResizableDemoWindowJfx(
            "Bar plot (X-continuous)",
            plotAssembler = createPlotAssembler()
        ).open()
    }
}