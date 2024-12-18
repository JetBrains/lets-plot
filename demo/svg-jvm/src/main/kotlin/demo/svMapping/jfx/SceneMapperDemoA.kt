/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.svMapping.jfx

import demo.common.utils.jfx.SvgViewerDemoWindowJfx
import demo.svgMapping.model.DemoModelA
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgSvgElement

fun main() {
    val svgGroup = DemoModelA.createModel()
    val svgRoot = SvgSvgElement(500.0, 500.0)
    svgRoot.children().add(svgGroup)
    SvgViewerDemoWindowJfx(
        "Svg Elements (A)",
        listOf(svgRoot)
    ).open()
}

