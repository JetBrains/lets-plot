/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.plotConfig

import demo.plot.common.model.plotConfig.BackgroundBlank

object BackgroundBlankBrowser {
    @JvmStatic
    fun main(args: Array<String>) {
        with(BackgroundBlank()) {
            @Suppress("UNCHECKED_CAST")
            (PlotConfigBrowserDemoUtil.show(
                "Plot background - blank (in a pink window)",
                plotSpecList(),
                backgroundColor = "pink"
            ))
        }
    }
}
