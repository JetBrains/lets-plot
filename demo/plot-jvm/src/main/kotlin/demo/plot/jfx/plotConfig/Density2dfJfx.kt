/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.jfx.plotConfig

import demo.common.util.demoUtils.jfx.PlotSpecsDemoWindowJfx
import demo.plot.common.model.plotConfig.Density2df

fun main() {
    with(Density2df()) {
        PlotSpecsDemoWindowJfx(
            "Density2df plot",
            plotSpecList()
        ).open()
    }
}

