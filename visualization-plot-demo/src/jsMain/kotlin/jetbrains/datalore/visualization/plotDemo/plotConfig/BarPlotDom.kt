package jetbrains.datalore.visualization.plotDemo.plotConfig

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.observable.property.ValueProperty
import jetbrains.datalore.visualization.base.svg.SvgNodeContainer
import jetbrains.datalore.visualization.base.svgMapper.dom.SvgRootDocumentMapper
import jetbrains.datalore.visualization.plot.MonolithicJs
import jetbrains.datalore.visualization.plot.builder.PlotContainer
import jetbrains.datalore.visualization.plotDemo.model.plotConfig.BarPlot
import org.w3c.dom.svg.SVGSVGElement
import kotlin.browser.document


//fun main() {
//    val svgRoot = DemoModel.createModel()
//    val mapper = SvgRootDocumentMapper(svgRoot)
//    SvgNodeContainer(svgRoot)
//    mapper.attachRoot()
//    document.getElementById("root")!!.appendChild(mapper.target)
//}

fun main() {
    with(BarPlot()) {
        @Suppress("UNCHECKED_CAST")
        val plotSpecList = plotSpecList() as List<MutableMap<String, Any>>
//        PlotConfigDemoUtil.show("Bar plot", plotSpecList, BatikMapperDemoFactory(), demoComponentSize)

        val svg = mapPlotToSvg(plotSpecList[0], demoComponentSize)
        document.getElementById("root")!!.appendChild(svg)
    }
}

//private fun createPlotElement(plotSpec: MutableMap<String, Any>, plotSize: DoubleVector) : HTMLDivElement {
//
//}

private fun mapPlotToSvg(plotSpec: MutableMap<String, Any>, plotSize: DoubleVector): SVGSVGElement {
    val plot = MonolithicJs.createPlot(plotSpec, null)
    val plotContainer = PlotContainer(plot, ValueProperty(plotSize))
    plotContainer.ensureContentBuilt()

    val svgRoot = plotContainer.svg
    val mapper = SvgRootDocumentMapper(svgRoot)
    SvgNodeContainer(svgRoot)
    mapper.attachRoot()
    return mapper.target
}

