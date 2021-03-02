/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.plotConfig

import jetbrains.datalore.plotDemo.model.plotConfig.FacetGridDemo
import jetbrains.datalore.vis.demoUtils.PlotSpecsViewerDemoWindowBatik
import java.awt.Dimension

object FacetGridBatik {
    @JvmStatic
    fun main(args: Array<String>) {
        with(FacetGridDemo()) {
            PlotSpecsViewerDemoWindowBatik.show(
                "Facet grid",
                plotSpecList(),
                2,
                Dimension(600, 400)
            )
        }
    }
}
