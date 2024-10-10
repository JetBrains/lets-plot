/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.batik.mapping.svg

import org.apache.batik.anim.dom.SVGOMElement
import org.apache.batik.anim.dom.SVGOMTextContentElement
import org.apache.batik.dom.svg.SVGOMPoint
import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.datamodel.mapping.framework.Mapper
import org.jetbrains.letsPlot.datamodel.svg.dom.*
import org.w3c.dom.Node
import org.w3c.dom.svg.SVGLocatable
import org.w3c.dom.svg.SVGTransformable

internal class SvgBatikPeer : SvgPlatformPeer {
    private val myMappingMap = HashMap<SvgNode, Mapper<out SvgNode, out Node>>()

    private fun ensureElementConsistency(source: SvgNode, target: Node) {
        if (source is SvgElement && target !is SVGOMElement) {
            throw IllegalStateException("Target of SvgElement must be SVGOMElement")
        }
    }

    private fun ensureLocatableConsistency(source: SvgNode, target: Node) {
        if (source is SvgLocatable && target !is SVGLocatable) {
            throw IllegalStateException("Target of SvgLocatable must be SVGLocatable")
        }
    }

    private fun ensureTextContentConsistency(source: SvgNode, target: Node) {
        if (source is SvgTextContent && target !is SVGOMTextContentElement) {
            throw IllegalStateException("Target of SvgTextContent must be SVGOMTextContentElement")
        }
    }

    private fun ensureTransformableConsistency(source: SvgNode, target: Node) {
        if (source is SvgTransformable && target !is SVGTransformable) {
            throw IllegalStateException("Target of SvgTransformable must be SVGTransformable")
        }
    }

    private fun ensureSourceTargetConsistency(source: SvgNode, target: Node) {
        ensureElementConsistency(source, target)
        ensureLocatableConsistency(source, target)
        ensureTextContentConsistency(source, target)
        ensureTransformableConsistency(source, target)
    }

    private fun ensureSourceRegistered(source: SvgNode) {
        if (!myMappingMap.containsKey(source)) {
            throw IllegalStateException("Trying to call platform peer method of unmapped node")
        }
    }

    fun registerMapper(source: SvgNode, mapper: SvgNodeMapper<out SvgNode, out Node>) {
        ensureSourceTargetConsistency(source, mapper.target)
        myMappingMap[source] = mapper
    }

    fun unregisterMapper(source: SvgNode) {
        myMappingMap.remove(source)
    }

    override fun getComputedTextLength(node: SvgTextContent): Double {
        ensureSourceRegistered(node as SvgNode)

        val target = myMappingMap[node]!!.target
        return (target as SVGOMTextContentElement).computedTextLength.toDouble()
    }

    private fun transformCoordinates(relative: SvgLocatable, point: DoubleVector, inverse: Boolean): DoubleVector {
        ensureSourceRegistered(relative as SvgNode)

        val relativeTarget = myMappingMap[relative]!!.target
        var matrix =
            (relativeTarget as SVGLocatable).getTransformToElement((relativeTarget as SVGOMElement).ownerSVGElement)
        if (inverse) {
            matrix = matrix.inverse()
        }
        val pt = SVGOMPoint(point.x.toFloat(), point.y.toFloat())
        val inversePt = pt.matrixTransform(matrix)
        return DoubleVector(inversePt.x.toDouble(), inversePt.y.toDouble())
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
        val bBox = (target as SVGLocatable).bBox
        val bbox =
            DoubleRectangle(bBox.x.toDouble(), bBox.y.toDouble(), bBox.width.toDouble(), bBox.height.toDouble())
//        println(bbox)
        return bbox
    }
}