/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.jfx.geom

import demo.plot.shared.model.geom.BarDemo
import demo.common.jfx.demoUtils.SvgViewerDemoWindowJfx

fun main() {
    with(BarDemo()) {
        SvgViewerDemoWindowJfx(
            "Bar geom",
            createSvgRoots(createModels())
        ).open()
    }
}
