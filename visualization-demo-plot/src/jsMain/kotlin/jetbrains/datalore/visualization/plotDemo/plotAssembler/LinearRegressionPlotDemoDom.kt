package jetbrains.datalore.visualization.plotDemo.plotAssembler

import jetbrains.datalore.visualization.base.browser.DomMapperDemoUtil
import jetbrains.datalore.visualization.base.svg.SvgSvgElement
import jetbrains.datalore.visualization.plotDemo.model.plotAssembler.LinearRegressionPlotDemo

/**
 * Called from generated HTML
 * Run with LinearRegressionDemoBrowser.kt
 */

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
