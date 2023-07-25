/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.batik.plotConfig

import demo.plot.common.model.plotConfig.TextAndLabel
import demo.common.batik.demoUtils.PlotSpecsDemoWindowBatik

fun main() {
    with (TextAndLabel()) {
        PlotSpecsDemoWindowBatik(
            "geom_text, geom_label",
            plotSpecList()
        ).open()
    }
}