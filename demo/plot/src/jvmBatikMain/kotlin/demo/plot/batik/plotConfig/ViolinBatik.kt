/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.plotConfig

import demo.plot.common.model.plotConfig.Violin
import demo.common.batik.demoUtils.PlotSpecsDemoWindowBatik

fun main() {
    with(Violin()) {
        PlotSpecsDemoWindowBatik(
            "Violin plot",
            plotSpecList()
        ).open()
    }
}