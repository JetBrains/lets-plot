/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.jfx.geom

import demo.plot.shared.model.geom.PathDemo
import demo.common.jfx.demoUtils.SvgViewerDemoWindowJfx

fun main() {
    with(PathDemo()) {
        SvgViewerDemoWindowJfx(
            "Path geom",
            createSvgRoots(createModels())
        ).open()
    }
}

