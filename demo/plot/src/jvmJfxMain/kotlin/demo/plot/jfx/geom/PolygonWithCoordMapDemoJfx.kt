/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.jfx.geom

import demo.common.jfx.demoUtils.SvgViewerDemoWindowJfx
import demo.plot.shared.model.geom.PolygonWithCoordMapDemo

fun main() {
    with(PolygonWithCoordMapDemo()) {
        SvgViewerDemoWindowJfx(
            "Polygon with CoordMap",
            createSvgRoots(createModels())
        ).open()
    }
}
