/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.plotConfig

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import jetbrains.datalore.plotDemo.model.plotConfig.LongTooltipText

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