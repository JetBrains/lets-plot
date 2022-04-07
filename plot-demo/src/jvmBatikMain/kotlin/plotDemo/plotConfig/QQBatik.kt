/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.plotConfig

import jetbrains.datalore.plotDemo.model.plotConfig.QQ
import jetbrains.datalore.vis.demoUtils.PlotSpecsDemoWindowBatik

fun main() {
    with(QQ()) {
        PlotSpecsDemoWindowBatik(
            "Q-Q plot",
            plotSpecList()
        ).open()
    }
}