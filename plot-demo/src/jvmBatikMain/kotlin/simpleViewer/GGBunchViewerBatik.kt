/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.simpleViewer

import jetbrains.datalore.plotDemo.model.plotConfig.GGBunch
import jetbrains.datalore.vis.swing.batik.PlotViewerWindow

object GGBunchViewerBatik {
    @JvmStatic
    fun main(args: Array<String>) {
        with(GGBunch()) {
            @Suppress("UNCHECKED_CAST")
            val plotSpecList = plotSpecList() as List<MutableMap<String, Any>>
            PlotViewerWindow(
                "GGBunch",
                plotSpecList.first(),
//                windowSize = Dimension(400, 300)
            ).open()
        }
    }
}
