/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.plotContainer

import jetbrains.datalore.plotDemo.model.plotContainer.BarPlotResizeDemo
import jetbrains.datalore.vis.demoUtils.PlotResizableDemoWindowBatik

fun main() {
    with(BarPlotResizeDemo.discreteX()) {
        PlotResizableDemoWindowBatik(
            "Bar plot (X-discrete)",
            plotAssembler = createPlotAssembler()
        ).open()
    }
}