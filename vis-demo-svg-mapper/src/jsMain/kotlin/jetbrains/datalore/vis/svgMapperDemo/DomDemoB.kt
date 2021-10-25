/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.svgMapperDemo

import jetbrains.datalore.vis.browser.DomMapperDemoUtil
import jetbrains.datalore.vis.svgDemoModel.b.DemoModelB

@OptIn(ExperimentalJsExport::class)
@JsExport
fun svgElementsDemo() {
    val svgRoot = DemoModelB.createModel()
    DomMapperDemoUtil.mapToDom(listOf(svgRoot), "root")
}