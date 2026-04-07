/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.swing.plotConfig

import demo.common.utils.swing.PlotSpecsDemoWindowSwing
import demo.plot.common.model.plotConfig.GGDeck

fun main() {
    with(GGDeck()) {
        PlotSpecsDemoWindowSwing(
            "Plot Deck",
            plotSpecList(),
            maxCol = 2
        ).open()
    }
}
