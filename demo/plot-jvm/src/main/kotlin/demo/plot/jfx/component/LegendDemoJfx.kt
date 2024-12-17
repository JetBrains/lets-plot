/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.jfx.component

import demo.common.util.demoUtils.jfx.SvgViewerDemoWindowJfx
import demo.plot.shared.model.component.LegendDemo

fun main() {
    with(LegendDemo()) {
        SvgViewerDemoWindowJfx(
            "Legend component (JFX SVG mapper)",
            createSvgRoots(createModels())
        ).open()
    }
}