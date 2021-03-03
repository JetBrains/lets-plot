/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.geom

import jetbrains.datalore.plotDemo.model.geom.PolygonWithCoordMapDemo
import jetbrains.datalore.vis.demoUtils.SvgViewerDemoWindowJfx

fun main() {
    with(PolygonWithCoordMapDemo()) {
        SvgViewerDemoWindowJfx(
            "Polygon with CoordMap",
            createSvgRoots(createModels())
        ).open()
    }
}
