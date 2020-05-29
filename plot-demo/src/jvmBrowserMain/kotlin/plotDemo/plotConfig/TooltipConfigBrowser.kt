/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.plotConfig

import jetbrains.datalore.plotDemo.model.plotConfig.TooltipConfig

object TooltipConfigBrowser {
    @JvmStatic
    fun main(args: Array<String>) {
        with(TooltipConfig()) {
            @Suppress("UNCHECKED_CAST")
            (PlotConfigBrowserDemoUtil.show(
                "Tooltip config plot",
                plotSpecList() as List<MutableMap<String, Any>>,
                demoComponentSize
            ))
        }
    }
}