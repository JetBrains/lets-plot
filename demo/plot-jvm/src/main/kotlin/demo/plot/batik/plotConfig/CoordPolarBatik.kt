/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.batik.plotConfig

import demo.common.util.demoUtils.batik.PlotSpecsDemoWindowBatik
import demo.plot.common.model.plotConfig.CoordPolarPlotSpecs


fun main() {
    with(CoordPolarPlotSpecs()) {
        PlotSpecsDemoWindowBatik(
            "coord_polar()",
            plotSpecList(),
            maxCol = 2
        ).open()
    }
}