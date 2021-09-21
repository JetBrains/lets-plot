/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.plotAssembler

import jetbrains.datalore.plotDemo.model.plotAssembler.LoessRegressionPlotDemo
import jetbrains.datalore.vis.browser.DomMapperDemoUtil
import jetbrains.datalore.vis.svg.SvgSvgElement

/**
 * Called from generated HTML
 * Run with LoessRegressionDemoBrowser.kt
 */

@OptIn(ExperimentalJsExport::class)
@JsExport
fun loessRegressionDemo() {
    with(LoessRegressionPlotDemoDom()) {
        DomMapperDemoUtil.mapToDom(svgRoots(), "root")
    }
}


class LoessRegressionPlotDemoDom : LoessRegressionPlotDemo() {

    fun svgRoots(): List<SvgSvgElement> {
        val plots = createPlots()
        return createSvgRootsFromPlots(plots)
    }
}
