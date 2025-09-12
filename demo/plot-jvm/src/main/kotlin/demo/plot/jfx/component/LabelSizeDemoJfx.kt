/*
 * Copyright (c) 2023. JetBrains s.r.o. 
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.jfx.component

import demo.common.utils.jfx.SvgViewerDemoWindowJfx
import demo.plot.shared.model.component.LabelSizeDemo

fun main() {
    with(LabelSizeDemo()) {
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
            "Label size and style",
            createSvgRoots(listOf(createModel())),
        ).open()
    }
}

