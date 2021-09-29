/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.plotConfig

import jetbrains.datalore.plotDemo.model.plotConfig.TooltipWithSampling

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