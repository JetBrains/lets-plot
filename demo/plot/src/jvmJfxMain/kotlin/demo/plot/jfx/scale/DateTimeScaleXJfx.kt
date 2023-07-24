/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.jfx.scale

import demo.common.jfx.demoUtils.PlotSpecsDemoWindowJfx
import demo.plot.shared.model.scale.DateTimeScaleX

fun main() {
    with(DateTimeScaleX()) {
        PlotSpecsDemoWindowJfx(
            "Datetime scale",
            plotSpecList()
        ).open()
    }
}

