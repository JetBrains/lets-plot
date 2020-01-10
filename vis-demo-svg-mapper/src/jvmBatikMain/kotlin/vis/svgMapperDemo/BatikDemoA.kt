/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.svgMapperDemo

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.vis.svg.SvgSvgElement
import jetbrains.datalore.vis.svgDemoModel.a.DemoModelA
import jetbrains.datalore.vis.demoUtils.BatikMapperDemoFrame

fun main() {
    val size = DoubleVector(500.0, 500.0)
    val svgGroup = DemoModelA.createModel()
    val svgRoot = SvgSvgElement(size.x, size.y)
    svgRoot.children().add(svgGroup)
    val svgRoots = listOf(svgRoot)
    BatikMapperDemoFrame.showSvg(
        svgRoots,
        size,
        "Svg Elements (A)"
    )
}
