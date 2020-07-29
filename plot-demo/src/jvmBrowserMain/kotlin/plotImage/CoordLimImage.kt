/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotImage

import jetbrains.datalore.plotDemo.model.plotConfig.CoordLim

object CoordLimImage {
    @JvmStatic
    fun main(args: Array<String>) {
        with(CoordLim()) {
            @Suppress("UNCHECKED_CAST")
            (PlotImageDemoUtil.show(
                "coord x/y limits",
                plotSpecList() as List<MutableMap<String, Any>>
            ))
        }
    }
}