/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.plotConfig

import jetbrains.datalore.plotDemo.model.plotConfig.MarginalLayersFacetsDemo
import jetbrains.datalore.vis.demoUtils.PlotSpecsDemoWindowBatik

fun main() {
    with(MarginalLayersFacetsDemo()) {
        PlotSpecsDemoWindowBatik(
            "Marginal layers (facets).",
            plotSpecList()
        ).open()
    }
}
