/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.browser.plotConfig

import demo.plot.common.model.plotConfig.GGToolbar

object GGToolbarBrowser {
    @JvmStatic
    fun main(args: Array<String>) {
        with(GGToolbar()) {
            @Suppress("UNCHECKED_CAST")
            (PlotConfigBrowserDemoUtil.show(
                "Plot Toolbar",
                plotSpecList(dark = false),
                backgroundColor = "white"
            ))
        }
    }
}
