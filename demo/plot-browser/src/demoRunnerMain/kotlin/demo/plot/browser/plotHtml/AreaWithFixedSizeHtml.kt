/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.browser.plotHtml

import demo.plot.common.model.plotConfig.Area
import org.jetbrains.letsPlot.commons.geometry.DoubleVector

object AreaWithFixedSizeHtml {
    @JvmStatic
    fun main(args: Array<String>) {
        with(Area()) {
            (PlotHtmlDemoUtil.show(
                "Area plot (fixed 300 x 150)",
                plotSpecList(),
                plotSize = DoubleVector(300.0, 150.0),
                preferredWidth = null
            ))
        }
    }
}
