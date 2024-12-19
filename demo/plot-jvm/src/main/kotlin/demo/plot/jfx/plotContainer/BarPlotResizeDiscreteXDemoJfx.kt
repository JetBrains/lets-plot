/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.jfx.plotContainer

import demo.common.utils.jfx.PlotResizableDemoWindowJfx
import demo.plot.shared.model.plotContainer.BarPlotResizeDemo

fun main() {
    with(BarPlotResizeDemo.discreteX()) {
        PlotResizableDemoWindowJfx(
            "Bar plot (X-discrete)",
            plotAssembler = createPlotAssembler()
        ).open()
    }
}