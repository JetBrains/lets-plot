/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.plotConfig

import jetbrains.datalore.plotDemo.model.plotConfig.MultiLineTooltip
import jetbrains.datalore.vis.demoUtils.PlotSpecsViewerDemoWindowBatik

object MultiLineTooltipBatik {
    @JvmStatic
    fun main(args: Array<String>) {
        with(MultiLineTooltip()) {
            PlotSpecsViewerDemoWindowBatik.show(
                "Multi-line tooltips plot",
                plotSpecList(),
                2
            )
        }
    }
}