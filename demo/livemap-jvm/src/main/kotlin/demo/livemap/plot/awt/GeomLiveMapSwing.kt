/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.livemap.plot.awt

import demo.common.utils.swing.PlotSpecsDemoWindowSwing
import demo.livemap.common.plot.LiveMap

fun main() {
    with(LiveMap()) {
        PlotSpecsDemoWindowSwing(
            "LiveMap",
            plotSpecList(),
            maxCol = 4
        ).open()
    }
}
