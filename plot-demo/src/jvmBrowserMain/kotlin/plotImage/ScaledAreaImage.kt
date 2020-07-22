/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotImage

import jetbrains.datalore.plotDemo.model.plotConfig.Area

object ScaledAreaImage {

    @JvmStatic
    fun main(args: Array<String>) {
        with(Area()) {
            @Suppress("UNCHECKED_CAST")
            (PlotImageDemoUtil.showScaled(
                "Scaled Area plot",
                plotSpecList().first() as MutableMap<String, Any>,
                demoComponentSize,
                listOf(1.0, 2.0, 4.0)
            ))
        }
    }
}
