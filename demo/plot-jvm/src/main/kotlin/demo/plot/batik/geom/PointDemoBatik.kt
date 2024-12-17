/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.batik.geom

import demo.common.util.demoUtils.batik.SvgViewerDemoWindowBatik
import demo.plot.shared.model.geom.PointDemo

fun main() {
    with(PointDemo()) {
        SvgViewerDemoWindowBatik(
            "Points SVG",
            createSvgRoots(createModels())
        ).open()
    }
}
