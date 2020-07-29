/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotImage

import jetbrains.datalore.plotDemo.model.plotConfig.Density2df

object Density2dfImage {
    @JvmStatic
    fun main(args: Array<String>) {
        with(Density2df()) {
            @Suppress("UNCHECKED_CAST")
            (PlotImageDemoUtil.show(
                "Density2df plot",
                plotSpecList().first() as MutableMap<String, Any>,
                scaleFactors = listOf(1.0, 2.0, 4.0)
            ))
        }
    }
}
