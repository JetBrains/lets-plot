/*
 * Copyright (c) 2023. JetBrains s.r.o. 
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.jfx.component

import demo.plot.shared.model.component.ScatterDemo
import demo.common.jfx.demoUtils.SvgViewerDemoWindowJfx

fun main() {
    with(ScatterDemo()) {
        SvgViewerDemoWindowJfx(
            "Point geom with scale breaks and limits",
            createSvgRoots(createModels())
        ).open()
    }
}
