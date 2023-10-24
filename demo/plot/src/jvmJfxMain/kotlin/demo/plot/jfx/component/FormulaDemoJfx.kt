/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.jfx.component

import demo.common.jfx.demoUtils.SvgViewerDemoWindowJfx
import demo.plot.shared.model.component.FormulaDemo

fun main() {
    with(FormulaDemo()) {
        SvgViewerDemoWindowJfx(
            "Formula",
            createSvgRoots(listOf(createModel()))
        ).open()
    }
}