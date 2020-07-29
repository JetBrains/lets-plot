/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotImage

import jetbrains.datalore.plotDemo.model.plotConfig.Area

object AreaImage {
    @JvmStatic
    fun main(args: Array<String>) {
        with(Area()) {
            @Suppress("UNCHECKED_CAST")
            (PlotImageDemoUtil.show(
                "Area plot",
                plotSpecList() as List<MutableMap<String, Any>>
            ))
        }
    }
}
