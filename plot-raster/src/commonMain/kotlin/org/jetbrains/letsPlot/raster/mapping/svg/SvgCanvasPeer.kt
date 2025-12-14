/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.mapping.svg

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.registration.Disposable
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
) : SvgPlatformPeer, Disposable {
    private val textMeasuringCanvas = canvasPeer.createCanvas(Vector(1, 1))
    private val myMappingMap = HashMap<SvgNode, Mapper<out SvgNode, out Element>>()
    var styleSheet: StyleSheet? = null
        private set

    fun applyStyleSheet(styleSheet: StyleSheet) {
        this.styleSheet = styleSheet
    }

    private fun ensureSourceRegistered(source: SvgNode) {
        if (!myMappingMap.containsKey(source)) {
            throw IllegalStateException("Trying to call platform peer method of unmapped node: ${source::class.simpleName}")
        }
    }

    fun registerMapper(source: SvgNode, mapper: SvgNodeMapper<out SvgNode, out Element>) {
        myMappingMap[source] = mapper
    }

    fun unregisterMapper(source: SvgNode) {
        myMappingMap.remove(source)?.target?.detached()
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
        return target.bBox
    }

    fun measureText(text: String, font: Font): TextMetrics {
        val context2d = textMeasuringCanvas.context2d
        context2d.setFont(font)
        return context2d.measureText(text)
    }

    override fun dispose() {
        textMeasuringCanvas.context2d.dispose()
    }
}