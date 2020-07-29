/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotImage

import jetbrains.datalore.plotDemo.model.plotConfig.GGBunch

object GGBunchImage {
    @JvmStatic
    fun main(args: Array<String>) {
        with(GGBunch()) {
            @Suppress("UNCHECKED_CAST")
            val plotSpecList = plotSpecList() as List<MutableMap<String, Any>>
            PlotImageDemoUtil.show(
                "GGBunch",
                plotSpecList
            )
        }
    }
}
