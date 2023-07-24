/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.jfx.plotConfig

import demo.plot.common.model.plotConfig.HVLine
import demo.common.jfx.demoUtils.PlotSpecsDemoWindowJfx

fun main() {
    with(HVLine()) {
        PlotSpecsDemoWindowJfx(
            "hline & vline plot",
            plotSpecList(),
            maxCol = 2
        ).open()
    }
}
