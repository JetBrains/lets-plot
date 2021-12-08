/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.plotConfig

import jetbrains.datalore.plotDemo.model.plotConfig.Area

object AreaBrowser {
    @JvmStatic
    fun main(args: Array<String>) {
        with(Area()) {
            @Suppress("UNCHECKED_CAST")
            (PlotConfigBrowserDemoUtil.show(
                "Area plot",
                plotSpecList(),
            ))
        }
    }
}
