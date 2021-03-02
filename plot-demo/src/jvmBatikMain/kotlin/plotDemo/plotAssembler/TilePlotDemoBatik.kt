/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.plotAssembler

import jetbrains.datalore.plotDemo.model.plotAssembler.TilePlotDemo
import jetbrains.datalore.vis.demoUtils.PlotObjectsViewerDemoWindowBatik

object TilePlotDemoBatik {
    @JvmStatic
    fun main(args: Array<String>) {
        with(TilePlotDemo()) {
            PlotObjectsViewerDemoWindowBatik.show(
                "Tile plot",
                plotList = createPlots()
            )
        }
    }
}
