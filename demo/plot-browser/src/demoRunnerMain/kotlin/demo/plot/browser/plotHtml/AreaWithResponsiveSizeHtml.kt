/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.browser.plotHtml

import demo.plot.common.model.plotConfig.Area

object AreaWithResponsiveSizeHtml {
    @JvmStatic
    fun main(args: Array<String>) {
        with(Area()) {
            (PlotHtmlDemoUtil.show(
                "Area plot (responsive size)",
                plotSpecList(),
                plotSize = null,
                preferredWidth = null
            ))
        }
    }
}
