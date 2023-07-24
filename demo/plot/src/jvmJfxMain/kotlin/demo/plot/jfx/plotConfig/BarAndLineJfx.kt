/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.jfx.plotConfig

import demo.plot.common.model.plotConfig.BarAndLine
import demo.common.jfx.demoUtils.PlotSpecsDemoWindowJfx

fun main() {
    with(BarAndLine()) {
        PlotSpecsDemoWindowJfx(
            "Bar & Line plot",
            plotSpecList()
        ).open()
    }
}


