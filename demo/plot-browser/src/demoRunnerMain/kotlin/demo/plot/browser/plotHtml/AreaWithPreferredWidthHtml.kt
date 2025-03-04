/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.browser.plotHtml

import demo.plot.common.model.plotConfig.Area

object AreaWithPreferredWidthHtml {
    @JvmStatic
    fun main(args: Array<String>) {
        with(Area()) {
            (PlotHtmlDemoUtil.show(
                "Area plot (datalore pref. width 200)",
                plotSpecList(),
                plotSize = null,
                preferredWidth = 200.0
            ))
        }
    }
}
