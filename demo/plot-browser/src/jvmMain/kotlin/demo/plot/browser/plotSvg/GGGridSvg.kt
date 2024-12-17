/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.browser.plotSvg

import demo.plot.common.model.plotConfig.GGGrid

object GGGridSvg {
    @JvmStatic
    fun main(args: Array<String>) {
        with(GGGrid()) {
            @Suppress("UNCHECKED_CAST")
            (PlotSvgDemoUtil.show(
                "Plot Grid",
                plotSpecList(),
//                demoComponentSize
            ))
        }
    }
}
