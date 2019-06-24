package jetbrains.datalore.visualization.base.svgMapper.dom

import jetbrains.datalore.mapper.core.Mapper
import jetbrains.datalore.mapper.core.MapperFactory
import jetbrains.datalore.visualization.base.svg.*
import jetbrains.datalore.visualization.base.svg.slim.SvgSlimNode
import jetbrains.datalore.visualization.base.svgMapper.dom.domUtil.DomUtil
import org.w3c.dom.Node

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