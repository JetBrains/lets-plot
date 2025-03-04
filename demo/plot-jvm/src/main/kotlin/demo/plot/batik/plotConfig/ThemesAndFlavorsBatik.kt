/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.batik.plotConfig

import demo.common.utils.batik.PlotSpecsDemoWindowBatik
import demo.plot.common.model.plotConfig.PrebuiltThemesAndFlavors

fun main() {
    with(PrebuiltThemesAndFlavors()) {
        PlotSpecsDemoWindowBatik(
            "Prebuilt Themes / Flavors (single)",
            plotSpecList(facets = false)
        ).open()
    }
}