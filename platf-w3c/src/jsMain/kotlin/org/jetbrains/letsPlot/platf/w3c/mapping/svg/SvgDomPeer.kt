/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.platf.w3c.mapping.svg

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import org.jetbrains.letsPlot.datamodel.mapping.framework.Mapper
import org.jetbrains.letsPlot.datamodel.svg.dom.*
import org.w3c.dom.Node
import org.w3c.dom.svg.SVGElement
import org.w3c.dom.svg.SVGGraphicsElement
import org.w3c.dom.svg.SVGSVGElement
import org.w3c.dom.svg.SVGTextContentElement

class SvgDomPeer : SvgPlatformPeer {
    private val myMappingMap = HashMap<SvgNode, Mapper<out SvgNode, out Node>>()

    private fun ensureSourceRegistered(source: SvgNode) {

        if (!myMappingMap.containsKey(source)) {
            throw IllegalStateException("Trying to call platform peer method of unmapped node")
        }
    }

    fun registerMapper(source: SvgNode, mapper: SvgNodeMapper<out SvgNode, out Node>) {
        myMappingMap[source] = mapper
    }

    fun unregisterMapper(source: SvgNode) {
        myMappingMap.remove(source)
    }

    override fun getComputedTextLength(node: SvgTextContent): Double {
        ensureSourceRegistered(node as SvgNode)

        val target = myMappingMap[node]!!.target
        return (target as SVGTextContentElement).getComputedTextLength().toDouble()
    }

    private fun transformCoordinates(relative: SvgLocatable, point: DoubleVector, inverse: Boolean): DoubleVector {
        ensureSourceRegistered(relative as SvgNode)

        val relativeTarget = myMappingMap[relative]!!.target

        return transformCoordinates(relativeTarget as SVGElement, point.x, point.y, inverse)
    }

    private fun transformCoordinates(relativeTarget: SVGElement, x: Double, y: Double, inverse: Boolean): DoubleVector {
        var matrix = (relativeTarget as SVGGraphicsElement).getCTM()
        if (inverse) {
            matrix = matrix!!.inverse()
        }
        val pt = relativeTarget.ownerSVGElement!!.createSVGPoint()
        pt.x = x
        pt.y = y
        val inversePoint = pt.matrixTransform(matrix!!)

        return DoubleVector(inversePoint.x, inversePoint.y)
    }

    fun inverseScreenTransform(relative: SvgElement, point: DoubleVector): DoubleVector {
        /**
        This breaks event dispatching in case of 'static' nodes which were mere generated but were not mapped
        for the sake of performance. See {@link SvgNodeSubtreeGeneratingSynchronizer} for large svg-based plots.
         */
        //ensureSourceRegistered(relative as SvgNode)

        val owner = relative.ownerSvgElement
        ensureSourceRegistered(owner!!)

        val ownerTarget = myMappingMap[owner]!!.target
        return inverseScreenTransform(ownerTarget as SVGSVGElement, point.x, point.y)
    }

    private fun inverseScreenTransform(ownerTarget: SVGSVGElement, x: Double, y: Double): DoubleVector {
        val matrix = ownerTarget.getScreenCTM()!!.inverse()
        var pt = ownerTarget.createSVGPoint()
        pt.x = x
        pt.y = y
        pt = pt.matrixTransform(matrix)
        return DoubleVector(pt.x, pt.y)
    }

    override fun invertTransform(relative: SvgLocatable, point: DoubleVector): DoubleVector =
            transformCoordinates(relative, point, true)

    override fun applyTransform(relative: SvgLocatable, point: DoubleVector): DoubleVector =
            transformCoordinates(relative, point, false)

    override fun getBBox(element: SvgLocatable): DoubleRectangle {
        ensureSourceRegistered(element as SvgNode)

        val target = myMappingMap[element]!!.target
        return getBoundingBox(target)
    }

    private fun getBoundingBox(target: Node): DoubleRectangle {
        val svgGraphicsElement = target as SVGGraphicsElement
        val bBox = svgGraphicsElement.getBBox()
        return DoubleRectangle(bBox.x, bBox.y, bBox.width, bBox.height)
    }
}