/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.geom

import demo.common.batik.demoUtils.SvgViewerDemoWindowBatik
import demo.plot.shared.model.geom.PointDemo

fun main() {
    with(PointDemo()) {
        SvgViewerDemoWindowBatik(
            "Points SVG",
            createSvgRoots(createModels())
        ).open()
    }
}
