/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.svgMapping

import demo.svgMapping.model.DemoModelB
import org.jetbrains.letsPlot.platf.w3c.mapping.util.SvgToW3c.generateDom

@JsExport
fun svgElementsDemoB() {
    val svgRoot = DemoModelB.createModel()
    generateDom(listOf(svgRoot), "root")
}