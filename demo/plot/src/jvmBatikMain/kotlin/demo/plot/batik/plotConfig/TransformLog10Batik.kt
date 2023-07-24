/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.plotConfig

import demo.plot.common.model.plotConfig.TransformLog10
import demo.common.batik.demoUtils.PlotSpecsDemoWindowBatik

fun main() {
    with(TransformLog10()) {
        PlotSpecsDemoWindowBatik(
            "'log10' transform.",
            plotSpecList()
        ).open()
    }
}
