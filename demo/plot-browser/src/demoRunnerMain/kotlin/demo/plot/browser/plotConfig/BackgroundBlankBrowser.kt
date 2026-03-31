/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.browser.plotConfig

import demo.plot.common.model.plotConfig.BackgroundBlank

fun main() {
    with(BackgroundBlank()) {
        PlotConfigBrowserDemoUtil.showJs(
            "Plot background - blank (in a pink window)",
            plotSpecList(),
            backgroundColor = "pink"
        )
        @Suppress("UNCHECKED_CAST")
        (Unit)
    }
}
