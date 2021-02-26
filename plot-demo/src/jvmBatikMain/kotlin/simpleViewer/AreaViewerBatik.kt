/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.simpleViewer

import jetbrains.datalore.plotDemo.model.plotConfig.Area
import jetbrains.datalore.vis.swing.simple.batik.PlotViewerWindow
import java.awt.Dimension

object AreaViewerBatik {
    @JvmStatic
    fun main(args: Array<String>) {
        with(Area()) {
            @Suppress("UNCHECKED_CAST")
            val plotSpecList = plotSpecList() as List<MutableMap<String, Any>>
            for (spec in plotSpecList) {
                PlotViewerWindow(
                    "Area plot",
                    spec,
                    null,
//                    Dimension(900, 700),
                    preserveAspectRatio = false
                ).open()
            }
        }
    }
}
