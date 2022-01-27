/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.scale

import jetbrains.datalore.plotDemo.model.scale.DateTimeAnnotation
import jetbrains.datalore.vis.demoUtils.PlotSpecsDemoWindowJfx

fun main() {
    with(DateTimeAnnotation()) {
        PlotSpecsDemoWindowJfx(
            "Datetime annotation",
            plotSpecList()
        ).open()
    }
}