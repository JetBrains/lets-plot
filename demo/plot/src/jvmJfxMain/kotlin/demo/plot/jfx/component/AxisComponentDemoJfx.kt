/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.jfx.component

import demo.common.jfx.demoUtils.SvgViewerDemoWindowJfx
import demo.plot.shared.model.component.AxisComponentDemo

fun main() {
    with(AxisComponentDemo()) {
        SvgViewerDemoWindowJfx(
            "Axis component (JFX SVG mapper)",
            createSvgRoots()
        ).open()
    }
}


