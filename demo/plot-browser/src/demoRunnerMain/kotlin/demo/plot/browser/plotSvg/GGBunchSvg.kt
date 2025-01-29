/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.browser.plotSvg

import demo.plot.common.model.plotConfig.GGBunch
import org.jetbrains.letsPlot.commons.geometry.DoubleVector

object GGBunchSvg {
    @JvmStatic
    fun main(args: Array<String>) {
        with(GGBunch()) {
            val plotSpecList = plotSpecList()
            PlotSvgDemoUtil.show(
                "ggbunch()",
                plotSpecList,
                DoubleVector(600.0, 600.0)
            )
        }
    }
}
