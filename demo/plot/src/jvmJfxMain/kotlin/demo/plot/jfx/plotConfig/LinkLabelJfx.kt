/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.jfx.plotConfig

import demo.common.jfx.demoUtils.PlotSpecsDemoWindowJfx
import demo.plot.common.model.plotConfig.LinkLabel

fun main() {
    with(LinkLabel()) {
        PlotSpecsDemoWindowJfx(
            "Link label demo",
            plotSpecList()
        ).open()
    }
}