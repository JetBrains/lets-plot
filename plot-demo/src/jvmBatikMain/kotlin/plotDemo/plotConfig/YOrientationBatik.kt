/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.plotConfig

import jetbrains.datalore.plotDemo.model.plotConfig.YOrientation
import jetbrains.datalore.vis.demoUtils.PlotSpecsDemoWindowBatik

fun main() {
    with(YOrientation()) {
        PlotSpecsDemoWindowBatik(
            "Y-orientation",
            plotSpecList(),
            maxCol = 3
        ).open()
    }
}