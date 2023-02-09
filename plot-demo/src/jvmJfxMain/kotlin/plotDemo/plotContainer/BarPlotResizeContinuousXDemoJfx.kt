/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.plotContainer

import jetbrains.datalore.plotDemo.model.plotContainer.BarPlotResizeDemo
import jetbrains.datalore.vis.demoUtils.PlotResizableDemoWindowJfx

fun main() {
    with(BarPlotResizeDemo.continuousX()) {
        PlotResizableDemoWindowJfx(
            "Bar plot (X-continuous)",
            plotAssembler = createPlotAssembler()
        ).open()
    }
}