/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.mapping.svg

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.canvas.CanvasPeer
import org.jetbrains.letsPlot.core.canvas.Font
import org.jetbrains.letsPlot.core.canvas.TextMetrics
import org.jetbrains.letsPlot.datamodel.mapping.framework.Mapper
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgLocatable
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgNode
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgPlatformPeer
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextContent
import org.jetbrains.letsPlot.datamodel.svg.style.StyleSheet
import org.jetbrains.letsPlot.raster.shape.Container
import org.jetbrains.letsPlot.raster.shape.Element
import org.jetbrains.letsPlot.raster.shape.Text
import org.jetbrains.letsPlot.raster.shape.breadthFirstTraversal

internal class SvgCanvasPeer(
    val canvasPeer: CanvasPeer,
) : SvgPlatformPeer {
    private val textMeasuringCanvas = canvasPeer.createCanvas(0, 0)
    private val myMappingMap = HashMap<SvgNode, Mapper<out SvgNode, out Element>>()

    var styleSheet: StyleSheet? = null
        private set

    fun applyStyleSheet(styleSheet: StyleSheet) {
        this.styleSheet = styleSheet
    }

    fun measureTextWidth(text: String, font: Font): Double {
        val ctx = textMeasuringCanvas.context2d
        ctx.save()
        ctx.setFont(font)
        val width = ctx.measureTextWidth(text)
        ctx.restore()
        return width
    }

    fun measureText(text: String, font: Font): TextMetrics {
        val ctx = textMeasuringCanvas.context2d
        ctx.save()
        ctx.setFont(font)
        val textMetrics = ctx.measureText(text)
        ctx.restore()

        return textMetrics
    }


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

    fun registerMapper(source: SvgNode, mapper: SvgNodeMapper<out SvgNode, out Element>) {
        myMappingMap[source] = mapper
    }

    fun unregisterMapper(source: SvgNode) {
        myMappingMap.remove(source)?.target?.release()
    }

    override fun getComputedTextLength(node: SvgTextContent): Double {
        error("UNSUPPORTED: getComputedTextLength")
    }

    @Suppress("UNUSED_PARAMETER")
    private fun transformCoordinates(relative: SvgLocatable, point: DoubleVector, inverse: Boolean): DoubleVector {
        error("UNSUPPORTED: transformCoordinates")
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

        if (target is Container) {
            breadthFirstTraversal(target).forEach {
                if (it is Text) {
                    it.layoutChildren()
                }
            }
        }
        return target.localBounds
    }
}