/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.svgMapperDemo

import jetbrains.datalore.vis.demoUtils.SvgViewerDemoWindowBatik
import jetbrains.datalore.vis.svgDemoModel.b.DemoModelB

fun main() {
    val svgRoot = DemoModelB.createModel()
    svgRoot.width().set(500.0)
    svgRoot.height().set(500.0)
    SvgViewerDemoWindowBatik(
        "Svg Elements (B)",
        listOf(svgRoot)
    ).open()
}
