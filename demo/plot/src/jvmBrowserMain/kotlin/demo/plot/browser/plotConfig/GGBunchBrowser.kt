/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.browser.plotConfig

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import demo.plot.common.model.plotConfig.GGBunch

object GGBunchBrowser {
    @JvmStatic
    fun main(args: Array<String>) {
        with(GGBunch()) {
            @Suppress("UNCHECKED_CAST")
            val plotSpecList = plotSpecList()
            PlotConfigBrowserDemoUtil.show(
                "GGBunch",
                plotSpecList,
                DoubleVector(600.0, 600.0)
            )
        }
    }
}
