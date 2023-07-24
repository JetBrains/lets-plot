/*
 * Copyright (c) 2023. JetBrains s.r.o. 
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.jfx.component

import demo.plot.shared.model.component.TextLabelSizeDemo
import demo.common.jfx.demoUtils.SvgViewerDemoWindowJfx

fun main() {
    with(TextLabelSizeDemo()) {
//        val demoModels = listOf(createModel())
//        val svgRoots = createSvgRoots(demoModels)
//        showSvg(
//            svgRoots,
//            listOf(JFX_PLOT_STYLESHEET),
//            demoComponentSize,
//            "Text label size and style"
//        )
//
        SvgViewerDemoWindowJfx(
            "Text label size and style",
            createSvgRoots(listOf(createModel())),
        ).open()
    }
}

