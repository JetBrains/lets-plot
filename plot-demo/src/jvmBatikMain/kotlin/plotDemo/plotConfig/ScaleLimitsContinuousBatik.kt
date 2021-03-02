/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.plotConfig

import jetbrains.datalore.plotDemo.model.plotConfig.ScaleLimitsContinuous
import jetbrains.datalore.vis.demoUtils.PlotSpecsViewerDemoWindowBatik
import java.awt.Dimension

object ScaleLimitsContinuousBatik {
    @JvmStatic
    fun main(args: Array<String>) {
        with(ScaleLimitsContinuous()) {
            PlotSpecsViewerDemoWindowBatik.show(
                "Scale limits (continuous)",
                plotSpecList(),
                2,
                Dimension(500, 200)
            )
        }
    }
}