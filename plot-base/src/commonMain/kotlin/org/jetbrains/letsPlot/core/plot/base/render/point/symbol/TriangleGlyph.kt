/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.point.symbol

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.datamodel.svg.dom.slim.SvgSlimElements
import org.jetbrains.letsPlot.datamodel.svg.dom.slim.SvgSlimShape

import kotlin.jvm.JvmOverloads
import kotlin.math.*

internal class TriangleGlyph @JvmOverloads constructor(
    location: DoubleVector,
    size: Double,
    stroke: Double,
    pointingUp: Boolean,
    pinnedToCentroid: Boolean = true,
    inscribedInSquare: Boolean = false
) : SingletonGlyph(
    createTriangleShape(
        location,
        size,
        stroke,
        pointingUp,
        pinnedToCentroid,
        inscribedInSquare
    )
) {

    override fun createShape(location: DoubleVector, width: Double): SvgSlimShape {
        throw IllegalStateException("Not applicable")
    }

    companion object {
        // equilateral triangle
        private val HEIGHT_TO_SIDE_RATIO = sin(PI / 3)
        private const val VERTICAL_OFFSET_RATIO = 1.0 / 12

        private fun createTriangleShape(
            location: DoubleVector,
            size: Double,
            stroke: Double,
            pointingUp: Boolean,
            pinnedToCentroid: Boolean,
            inscribedInSquare: Boolean
        ): SvgSlimShape {
            val half = size / 2
            val height = if (inscribedInSquare) {
                val outerHeight = size + stroke
                val strokeSectionWidth = sqrt(5.0) * stroke
                outerHeight - stroke / 2.0 - strokeSectionWidth / 2.0
            } else
                HEIGHT_TO_SIDE_RATIO * size
            val base = if (inscribedInSquare)
                height
            else
                size

            val halfOfHeightVOffset = (height - size) / 2.0
            val vOffset = when {
                pinnedToCentroid -> height * VERTICAL_OFFSET_RATIO - halfOfHeightVOffset
                inscribedInSquare -> halfOfHeightVOffset
                else -> -stroke / 4.0
            }

            val x: List<Double>
            val y: List<Double>
            var dy = (size - height) / 2
            if (pointingUp) {
                dy -= vOffset
                x = listOf(base / 2, base, 0.0)
                y = listOf(0.0 + dy, height + dy, height + dy)
            } else {
                dy += vOffset
                x = listOf(0.0, base, base / 2)
                y = listOf(0.0 + dy, 0.0 + dy, height + dy)
            }

            val ox = location.x - base / 2
            val oy = location.y - half

            val pathData = GlyphUtil.buildPathData(
                x.map { it + ox },
                y.map { it + oy }
            )
            return SvgSlimElements.path(pathData)
        }
    }
}
