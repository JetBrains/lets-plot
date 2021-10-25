/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.plotAssembler

import jetbrains.datalore.plotDemo.model.plotAssembler.LinearRegressionPlotDemo
import jetbrains.datalore.vis.browser.DomMapperDemoUtil
import jetbrains.datalore.vis.svg.SvgSvgElement

/**
 * Called from generated HTML
 * Run with LinearRegressionDemoBrowser.kt
 */

@OptIn(ExperimentalJsExport::class)
@JsExport
fun linearRegressionDemo() {
    with(LinearRegressionPlotDemoDom()) {
        DomMapperDemoUtil.mapToDom(svgRoots(), "root")
    }
}


class LinearRegressionPlotDemoDom : LinearRegressionPlotDemo() {

    fun svgRoots(): List<SvgSvgElement> {
        val plots = createPlots()
        return createSvgRootsFromPlots(plots)
    }
}
