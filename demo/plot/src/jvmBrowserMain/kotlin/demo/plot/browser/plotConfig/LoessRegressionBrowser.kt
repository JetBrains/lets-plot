/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.plotConfig

import demo.plot.common.model.plotConfig.LoessRegression

object LoessRegressionBrowser {
    @JvmStatic
    fun main(args: Array<String>) {
        with(LoessRegression()) {
            @Suppress("UNCHECKED_CAST")
            (PlotConfigBrowserDemoUtil.show(
                "Area plot",
                plotSpecList(),
            ))
        }
    }
}
