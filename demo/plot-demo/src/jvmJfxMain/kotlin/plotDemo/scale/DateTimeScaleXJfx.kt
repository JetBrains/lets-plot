/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.scale

import jetbrains.datalore.plotDemo.model.scale.DateTimeScaleX
import jetbrains.datalore.vis.demoUtils.PlotSpecsDemoWindowJfx

fun main() {
    with(DateTimeScaleX()) {
        PlotSpecsDemoWindowJfx(
            "Datetime scale",
            plotSpecList()
        ).open()
    }
}

