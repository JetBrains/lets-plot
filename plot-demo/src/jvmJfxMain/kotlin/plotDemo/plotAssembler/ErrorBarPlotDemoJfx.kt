/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.plotAssembler

import jetbrains.datalore.plotDemo.model.plotAssembler.ErrorBarPlotDemo
import jetbrains.datalore.vis.demoUtils.PlotObjectsDemoWindowJfx

fun main() {
    with(ErrorBarPlotDemo()) {
        PlotObjectsDemoWindowJfx(
            "Error-bar plot",
            plotList = createPlots()
        ).open()
    }
}
