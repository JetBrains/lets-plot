/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.batik.component

import demo.common.util.demoUtils.batik.SvgViewerDemoWindowBatik
import demo.plot.shared.model.component.LegendDemo

fun main() {
    with(LegendDemo()) {
        SvgViewerDemoWindowBatik(
            "Legend component (Batik)",
            createSvgRoots(createModels())
        ).open()
    }
}
