/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.batik.component

import demo.common.utils.batik.SvgViewerDemoWindowBatik
import demo.plot.shared.model.component.AxisComponentDemo

fun main() {
    with(AxisComponentDemo()) {
        SvgViewerDemoWindowBatik(
            "Axis component (Batik)",
            createSvgRoots()
        ).open()
    }
}

