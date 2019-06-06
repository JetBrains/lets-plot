package jetbrains.datalore.visualization.base.svgMapper.jfx

import javafx.scene.Node
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.mapper.core.Mapper
import jetbrains.datalore.visualization.base.svg.SvgLocatable
import jetbrains.datalore.visualization.base.svg.SvgNode
import jetbrains.datalore.visualization.base.svg.SvgPlatformPeer
import jetbrains.datalore.visualization.base.svg.SvgTextContent

internal class SvgAwtPeer : SvgPlatformPeer {
    private val myMappingMap = HashMap<SvgNode, Mapper<out SvgNode, out Node>>()

//    private fun ensureElementConsistency(source: SvgNode, target: Node) {
//        if (source is SvgElement && target !is SVGOMElement) {
//            throw IllegalStateException("Target of SvgElement must be SVGOMElement")
//        }
//    }

//    private fun ensureLocatableConsistency(source: SvgNode, target: Node) {
//        if (source is SvgLocatable && target !is SVGLocatable) {
//            throw IllegalStateException("Target of SvgLocatable must be SVGLocatable")
//        }
//    }

//    private fun ensureTextContentConsistency(source: SvgNode, target: Node) {
//        if (source is SvgTextContent && target !is SVGOMTextContentElement) {
//            throw IllegalStateException("Target of SvgTextContent must be SVGOMTextContentElement")
//        }
//    }

//    private fun ensureTransformableConsistency(source: SvgNode, target: Node) {
//        if (source is SvgTransformable && target !is SVGTransformable) {
//            throw IllegalStateException("Target of SvgTransformable must be SVGTransformable")
//        }
//    }

//    private fun ensureSourceTargetConsistency(source: SvgNode, target: Node) {
//        ensureElementConsistency(source, target)
//        ensureLocatableConsistency(source, target)
//        ensureTextContentConsistency(source, target)
//        ensureTransformableConsistency(source, target)
//    }

//    private fun ensureSourceRegistered(source: SvgNode) {
//        if (!myMappingMap.containsKey(source)) {
//            throw IllegalStateException("Trying to call platform peer method of unmapped node")
//        }
//    }

    fun registerMapper(source: SvgNode, mapper: SvgNodeMapper<out SvgNode, out Node>) {
//        ensureSourceTargetConsistency(source, mapper.target)
        myMappingMap[source] = mapper
    }

    fun unregisterMapper(source: SvgNode) {
        myMappingMap.remove(source)
    }

    override fun getComputedTextLength(node: SvgTextContent): Double {
        TODO()
    }

    private fun transformCoordinates(relative: SvgLocatable, point: DoubleVector, inverse: Boolean): DoubleVector {
        TODO()
    }

    override fun invertTransform(relative: SvgLocatable, point: DoubleVector): DoubleVector {
        return transformCoordinates(relative, point, true)
    }

    override fun applyTransform(relative: SvgLocatable, point: DoubleVector): DoubleVector {
        return transformCoordinates(relative, point, false)
    }

    override fun getBBox(element: SvgLocatable): DoubleRectangle {
        TODO()
    }
}