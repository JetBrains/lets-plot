/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.svgMapperDemo

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.vis.demoUtils.SceneMapperDemoFrame
import jetbrains.datalore.vis.svgDemoModel.b.DemoModelB

fun main() {
    val svgRoots = listOf(DemoModelB.createModel())
    SceneMapperDemoFrame.showSvg(
        svgRoots,
        listOf("/svg-demo-model-b.css"),
        DoubleVector(500.0, 300.0),
        "Svg Elements (B)"
    )
}
