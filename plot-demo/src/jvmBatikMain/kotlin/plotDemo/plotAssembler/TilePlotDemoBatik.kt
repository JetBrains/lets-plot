/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.plotAssembler

import jetbrains.datalore.plotDemo.model.plotAssembler.TilePlotDemo
import jetbrains.datalore.vis.demoUtils.PlotObjectsViewerDemoWindowBatik

fun main() {
    with(TilePlotDemo()) {
        PlotObjectsViewerDemoWindowBatik(
            "Tile plot",
            plotList = createPlots()
        ).open()
    }
}
