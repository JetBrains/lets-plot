/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.jfx.plotConfig

import demo.common.util.demoUtils.jfx.PlotSpecsDemoWindowJfx
import demo.plot.common.model.plotConfig.ColorScalesViridis
import java.awt.Dimension

fun main() {
    with(ColorScalesViridis()) {
        PlotSpecsDemoWindowJfx(
            "Color Scales 'Viridis'",
            plotSpecList(),
            plotSize = Dimension(600, 100),
            maxCol = 2
        ).open()
    }
}
