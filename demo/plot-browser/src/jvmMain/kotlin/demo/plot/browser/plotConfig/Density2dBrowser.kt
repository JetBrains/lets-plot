/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.browser.plotConfig

import demo.plot.common.model.plotConfig.Density2d

object Density2dBrowser {
    @JvmStatic
    fun main(args: Array<String>) {
        with(Density2d()) {
            @Suppress("UNCHECKED_CAST")
            (PlotConfigBrowserDemoUtil.show(
                "Density2d plot",
                plotSpecList(),
//                demoComponentSize
            ))
        }
    }
}
