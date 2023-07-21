/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.svgMapping

import org.jetbrains.letsPlot.datamodel.svg.dom.SvgSvgElement
import demo.svgMapping.model.DemoModelA
import org.jetbrains.letsPlot.platf.w3c.mapping.util.SvgToW3c.generateDom

fun svgElementsDemoA() {
    val svgGroup = DemoModelA.createModel()
    val svgRoot = SvgSvgElement(500.0, 500.0)
    svgRoot.children().add(svgGroup)

    generateDom(listOf(svgRoot), "root")
}