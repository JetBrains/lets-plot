/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.jfx.geom

import demo.common.util.demoUtils.jfx.SvgViewerDemoWindowJfx
import demo.plot.shared.model.geom.PointDemo

fun main() {
    with(PointDemo()) {
        SvgViewerDemoWindowJfx(
            "Point geom",
            createSvgRoots(createModels())
        ).open()
    }
}