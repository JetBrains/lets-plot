/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.jfx.plotConfig

import demo.common.util.demoUtils.jfx.PlotSpecsDemoWindowJfx
import demo.plot.common.model.plotConfig.CoordPolarPlotSpecs

fun main() {
    with(CoordPolarPlotSpecs()) {
        PlotSpecsDemoWindowJfx(
            "coord_polar() plot",
            plotSpecList()
        ).open()
    }
}
