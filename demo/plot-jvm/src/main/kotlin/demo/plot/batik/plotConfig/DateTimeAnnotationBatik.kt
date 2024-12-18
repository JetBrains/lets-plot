/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.batik.plotConfig

import demo.common.utils.batik.PlotSpecsDemoWindowBatik
import demo.plot.shared.model.scale.DateTimeAnnotation
import java.awt.Dimension

fun main() {
    with(DateTimeAnnotation()) {
        PlotSpecsDemoWindowBatik(
            "DateTime annotation",
            plotSpecList(),
            2,
            Dimension(600, 400)
        ).open()
    }
}