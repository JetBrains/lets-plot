package jetbrains.datalore.visualization.base.svgToDom

import jetbrains.datalore.mapper.core.Mapper
import jetbrains.datalore.mapper.core.MapperFactory
import jetbrains.datalore.visualization.base.svg.*
import jetbrains.datalore.visualization.base.svg.slim.SvgSlimElements
import jetbrains.datalore.visualization.base.svg.slim.SvgSlimNode
import jetbrains.datalore.visualization.base.svgToDom.domUtil.DomUtil
import org.w3c.dom.Node
import org.w3c.dom.Text
import org.w3c.dom.svg.SVGElement
import kotlin.browser.document

class SvgNodeMapperFactory(private val myPeer: SvgDomPeer): MapperFactory<SvgNode, Node> {

    override fun createMapper(source: SvgNode): Mapper<out SvgNode, out Node> =
            when(source) {
                is SvgImageElement -> {
                    var s = source
                    if (s is SvgImageElementEx) {
                        s = s.asImageElement(RGBEncoderDom())
                    }
                    SvgElementMapper(s, DomUtil.generateElement(source), myPeer)
                }
                is SvgElement -> SvgElementMapper(source, DomUtil.generateElement(source), myPeer)
                is SvgTextNode -> SvgTextNodeMapper(source, DomUtil.generateTextElement(source), myPeer)
                is SvgSlimNode -> SvgNodeMapper(source, DomUtil.generateSlimNode(source), myPeer)
                else -> throw IllegalStateException("Unsupported SvgNode ${source::class}")
            }
}