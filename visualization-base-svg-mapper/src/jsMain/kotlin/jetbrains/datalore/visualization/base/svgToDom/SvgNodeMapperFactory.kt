package jetbrains.datalore.visualization.base.svgToDom

import jetbrains.datalore.mapper.core.Mapper
import jetbrains.datalore.mapper.core.MapperFactory
import jetbrains.datalore.visualization.base.svg.*
import org.w3c.dom.Node
import org.w3c.dom.svg.SVGElement
import kotlin.browser.document

class SvgNodeMapperFactory(private val myPeer: SvgDomPeer): MapperFactory<SvgNode, Node> {

    override fun createMapper(source: SvgNode): Mapper<out SvgNode, out Node> =
            when(source) {
                is SvgEllipseElement -> SvgElementMapper(source, createSVGElement("ellipse"), myPeer)
                is SvgCircleElement -> SvgElementMapper(source, createSVGElement("circle"), myPeer)
                is SvgRectElement -> SvgElementMapper(source, createSVGElement("rect"), myPeer)
                is SvgTextElement -> SvgElementMapper(source, createSVGElement("text"), myPeer)
                is SvgPathElement -> SvgElementMapper(source, createSVGElement("path"), myPeer)
                is SvgLineElement -> SvgElementMapper(source, createSVGElement("line"), myPeer)
                is SvgSvgElement -> SvgElementMapper(source, createSVGElement("svg"), myPeer)
                is SvgGElement -> SvgElementMapper(source, createSVGElement("g"), myPeer)
                is SvgStyleElement -> SvgElementMapper(source, createSVGElement("style"), myPeer)
                is SvgTextNode -> SvgTextNodeMapper(source, document.createTextNode(""), myPeer)
                is SvgTSpanElement -> SvgElementMapper(source, createSVGElement("tspan"), myPeer)
                is SvgDefsElement -> SvgElementMapper(source, createSVGElement("defs"), myPeer)
                is SvgClipPathElement -> SvgElementMapper(source, createSVGElement("clipPath"), myPeer)
                is SvgImageElement -> {
                    var s = source
                    if (s is SvgImageElementEx) {
                        s = s.asImageElement(RGBEncoderDom())
                    }
                    SvgElementMapper(s, createSVGElement("image"), myPeer)
                }
                else -> throw IllegalStateException("Unsupported SvgNode")
            }

    private fun createSVGElement(name: String): SVGElement =
            document.createElementNS("http://www.w3.org/2000/svg", name) as SVGElement
}