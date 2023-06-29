/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.svgMapper.jfx

import javafx.scene.Node
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.unsupported.UNSUPPORTED
import org.jetbrains.letsPlot.datamodel.mapping.framework.Mapper
import org.jetbrains.letsPlot.datamodel.svg.style.StyleSheet
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgLocatable
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgNode
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgPlatformPeer
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextContent

class SvgJfxPeer : SvgPlatformPeer {
    private val myMappingMap = HashMap<SvgNode, Mapper<out SvgNode, out Node>>()

    var styleSheet: StyleSheet? = null
        private set

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

    private fun ensureSourceRegistered(source: SvgNode) {
        if (!myMappingMap.containsKey(source)) {
            throw IllegalStateException("Trying to call platform peer method of unmapped node: ${source::class.simpleName}")
        }
    }

    fun registerMapper(source: SvgNode, mapper: SvgNodeMapper<out SvgNode, out Node>) {
        myMappingMap[source] = mapper
    }

    fun unregisterMapper(source: SvgNode) {
        myMappingMap.remove(source)
    }

    override fun getComputedTextLength(node: SvgTextContent): Double {
        UNSUPPORTED("getComputedTextLength")
    }

    @Suppress("UNUSED_PARAMETER")
    private fun transformCoordinates(relative: SvgLocatable, point: DoubleVector, inverse: Boolean): DoubleVector {
        UNSUPPORTED("getComputedTextLength")
    }

    override fun invertTransform(relative: SvgLocatable, point: DoubleVector): DoubleVector {
        return transformCoordinates(relative, point, true)
    }

    override fun applyTransform(relative: SvgLocatable, point: DoubleVector): DoubleVector {
        return transformCoordinates(relative, point, false)
    }

    override fun getBBox(element: SvgLocatable): DoubleRectangle {
        ensureSourceRegistered(element as SvgNode)
        val target = myMappingMap[element]!!.target

        // Otherwise Node will have outdated bounds if changes made by CSS (JFX apply CSS on next pulse)
        target.applyCss()

        val bounds = target.boundsInParent!!
        if (bounds.isEmpty) {
            throw IllegalStateException("Undefined target node bounds: ${target::class.simpleName}")
        }

        return DoubleRectangle(bounds.minX, bounds.minY, bounds.width, bounds.height)
    }

    fun applyStyleSheet(styleSheet: StyleSheet) {
        this.styleSheet = styleSheet
    }
}