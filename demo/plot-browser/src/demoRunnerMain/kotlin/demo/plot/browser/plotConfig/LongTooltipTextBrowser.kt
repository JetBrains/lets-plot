/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.browser.plotConfig

import demo.plot.common.model.plotConfig.LongTooltipText
import org.jetbrains.letsPlot.commons.geometry.DoubleVector

object LongTooltipTextBrowser {
    @JvmStatic
    fun main(args: Array<String>) {
        with(LongTooltipText()) {
            @Suppress("UNCHECKED_CAST")
            (PlotConfigBrowserDemoUtil.show(
                "Long text in tooltip",
                plotSpecList(),
                plotSize = DoubleVector(500.0, 1000.0)
            ))
        }
    }
}