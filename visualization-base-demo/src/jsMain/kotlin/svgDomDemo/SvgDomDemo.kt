package svgDomDemo

import jetbrains.datalore.visualization.base.svg.SvgGElement
import jetbrains.datalore.visualization.base.svg.SvgNodeContainer
import jetbrains.datalore.visualization.base.svg.SvgSvgElement
import jetbrains.datalore.visualization.base.svgToDom.SvgRootDocumentMapper
import jetbrains.datalore.visualization.baseDemo.svgCanvasDemo.model.DemoModel
import kotlin.browser.document

class SvgDomDemo {
    fun show(rootGroup: SvgGElement) {
        val rootElement = SvgSvgElement()
        rootElement.children().add(rootGroup)
        val mapper = SvgRootDocumentMapper(rootElement)
        SvgNodeContainer(rootElement)
        mapper.attachRoot()
        document.getElementById("root")!!.appendChild(mapper.target)
    }
}

fun main() {
    val model = DemoModel.createModel()
    SvgDomDemo().show(model)
}