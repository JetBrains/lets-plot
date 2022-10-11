/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.plotConfig

import jetbrains.datalore.plotDemo.model.plotConfig.TextAndLabel
import jetbrains.datalore.vis.demoUtils.PlotSpecsDemoWindowBatik

fun main() {
    with (TextAndLabel()) {
        PlotSpecsDemoWindowBatik(
            "geom_text, geom_label",
            plotSpecList()
        ).open()
    }
}