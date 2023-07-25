/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.browser.plotConfig

import demo.plot.common.model.plotConfig.PlotGrid

object PlotGridBrowser {
    @JvmStatic
    fun main(args: Array<String>) {
        with(PlotGrid()) {
            @Suppress("UNCHECKED_CAST")
            (PlotConfigBrowserDemoUtil.show(
                "Plot Grid",
                plotSpecList(),
            ))
        }
    }
}
