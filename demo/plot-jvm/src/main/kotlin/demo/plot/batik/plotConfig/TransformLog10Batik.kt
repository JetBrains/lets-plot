/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.batik.plotConfig

import demo.common.utils.batik.PlotSpecsDemoWindowBatik
import demo.plot.common.model.plotConfig.TransformLog10

fun main() {
    with(TransformLog10()) {
        PlotSpecsDemoWindowBatik(
            "'log10' transform.",
            plotSpecList()
        ).open()
    }
}
