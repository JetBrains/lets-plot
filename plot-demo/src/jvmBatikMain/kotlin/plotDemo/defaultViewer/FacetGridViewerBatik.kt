/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.defaultViewer

import jetbrains.datalore.plotDemo.model.plotConfig.FacetGridDemo
import jetbrains.datalore.vis.swing.batik.PlotViewerWindowBatik

fun main(args: Array<String>) {
    with(FacetGridDemo()) {
        @Suppress("UNCHECKED_CAST")
        val plotSpecList = plotSpecList() as List<MutableMap<String, Any>>
        PlotViewerWindowBatik(
            "Facet grid",
            null,
            plotSpecList.last()
//                    Dimension(900, 700)
        ).open()
    }
}
