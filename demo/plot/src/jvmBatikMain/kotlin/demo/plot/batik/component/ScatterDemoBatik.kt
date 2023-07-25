/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.batik.component

import demo.plot.shared.model.component.ScatterDemo
import demo.common.batik.demoUtils.SvgViewerDemoWindowBatik

fun main() {
    with(ScatterDemo()) {
        SvgViewerDemoWindowBatik(
            "Point geom with scale breaks and limits",
            createSvgRoots(createModels())
        ).open()
    }
}
