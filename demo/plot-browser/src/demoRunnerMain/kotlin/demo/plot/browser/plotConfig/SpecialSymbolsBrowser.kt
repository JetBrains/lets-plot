/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.browser.plotConfig

import demo.plot.common.model.plotConfig.SpecialSymbols

object SpecialSymbolsBrowser {
    @JvmStatic
    fun main(args: Array<String>) {
        with(SpecialSymbols()) {
            @Suppress("UNCHECKED_CAST")
            (PlotConfigBrowserDemoUtil.show(
                "Special symbols",
                listOf(plotSpec()),
//                demoComponentSize
            ))
        }
    }
}
