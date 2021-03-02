/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.plotConfig

import jetbrains.datalore.plotDemo.model.plotConfig.MercatorProjection
import jetbrains.datalore.vis.demoUtils.PlotSpecsViewerDemoWindowBatik
import java.awt.Dimension

object MercatorProjectionBatik {
    @JvmStatic
    fun main(args: Array<String>) {
        with(MercatorProjection()) {
            PlotSpecsViewerDemoWindowBatik.show(
                "Mercator projection",
                plotSpecList(),
                2,
                Dimension(400, 300)
            )
        }
    }
}
