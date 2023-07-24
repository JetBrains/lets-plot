/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.jfx.scale

import demo.common.jfx.demoUtils.PlotSpecsDemoWindowJfx
import demo.plot.shared.model.scale.DateTimeAnnotation

fun main() {
    with(DateTimeAnnotation()) {
        PlotSpecsDemoWindowJfx(
            "Datetime annotation",
            plotSpecList()
        ).open()
    }
}