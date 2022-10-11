/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.plotConfig

import jetbrains.datalore.plotDemo.model.plotConfig.TextAndLabel
import jetbrains.datalore.vis.demoUtils.PlotSpecsDemoWindowJfx

fun main() {
    with(TextAndLabel()) {
        PlotSpecsDemoWindowJfx(
            "geom_text, geom_label",
            plotSpecList()
        ).open()
    }
}
