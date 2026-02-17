/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.swing.plotConfig

import demo.common.utils.swing.PlotSpecsDemoWindowSwing
import demo.plot.common.model.plotConfig.GGToolbar
import java.awt.Color

fun main() {
    with(GGToolbar()) {
        PlotSpecsDemoWindowSwing(
            "Plot Toolbar",
            plotSpecList(true),
            maxCol = 1,
            background = Color.BLACK
        ).open()
    }
}
