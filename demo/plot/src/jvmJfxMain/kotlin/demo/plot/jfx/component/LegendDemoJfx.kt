/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.jfx.component

import demo.plot.shared.model.component.LegendDemo
import demo.common.jfx.demoUtils.SvgViewerDemoWindowJfx

fun main() {
    with(LegendDemo()) {
        SvgViewerDemoWindowJfx(
            "Legend component (JFX SVG mapper)",
            createSvgRoots(createModels())
        ).open()
    }
}