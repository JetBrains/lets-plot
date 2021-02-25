/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.simpleViewer

import jetbrains.datalore.plotDemo.model.plotConfig.FacetGridDemo
import jetbrains.datalore.vis.swing.simple.batik.PlotViewerWindow
import java.awt.Dimension

object FacetGridViewerBatik {
    @JvmStatic
    fun main(args: Array<String>) {
        with(FacetGridDemo()) {
            @Suppress("UNCHECKED_CAST")
            val plotSpecList = plotSpecList() as List<MutableMap<String, Any>>
            PlotViewerWindow(
                "Facet grid",
                plotSpecList.last(),
                null
//                    Dimension(900, 700)
            ).open()
        }
    }
}
