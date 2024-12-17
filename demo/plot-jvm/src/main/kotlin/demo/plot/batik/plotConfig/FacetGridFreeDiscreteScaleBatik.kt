/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.batik.plotConfig

import demo.common.util.demoUtils.batik.PlotSpecsDemoWindowBatik
import demo.plot.common.model.plotConfig.FacetGridFreeDiscreteScale

fun main() {
    try {
        with(FacetGridFreeDiscreteScale()) {
            PlotSpecsDemoWindowBatik(
                "Facet grid, free discrete scale",
                plotSpecList()
            ).open()
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
