/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.plotConfig

import jetbrains.datalore.plotDemo.model.plotConfig.FacetWrapDirVDemo
import jetbrains.datalore.vis.demoUtils.PlotSpecsViewerDemoWindowBatik
import java.awt.Dimension

object FacetWrapDirVBatik {
    @JvmStatic
    fun main(args: Array<String>) {
        with(FacetWrapDirVDemo()) {
            PlotSpecsViewerDemoWindowBatik.show(
                "Facet wrap",
                plotSpecList(),
                2,
                Dimension(600, 600)
            )
        }
    }
}
