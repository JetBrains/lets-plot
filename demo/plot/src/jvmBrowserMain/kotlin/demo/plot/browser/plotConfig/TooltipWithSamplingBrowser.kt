/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.browser.plotConfig

import demo.plot.common.model.plotConfig.TooltipWithSampling

object TooltipWithSamplingBrowser {

    @JvmStatic
    fun main(args: Array<String>) {
        with(TooltipWithSampling()) {
            @Suppress("UNCHECKED_CAST")
            (PlotConfigBrowserDemoUtil.show(
                "Tooltip config plot",
                plotSpecList(),
//                demoComponentSize
            ))
        }
    }
}