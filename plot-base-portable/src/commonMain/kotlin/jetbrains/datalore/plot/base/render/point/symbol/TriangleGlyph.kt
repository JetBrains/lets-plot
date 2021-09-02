/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.render.point.symbol

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.vis.svg.slim.SvgSlimElements
import jetbrains.datalore.vis.svg.slim.SvgSlimShape

import kotlin.jvm.JvmOverloads
import kotlin.math.PI
import kotlin.math.sin

internal class TriangleGlyph @JvmOverloads constructor(
    location: DoubleVector,
    size: Double,
    pointingUp: Boolean,
    inscribedInSquare: Boolean = false
) : SingletonGlyph(
    createTriangleShape(
        location,
        size,
        pointingUp,
        inscribedInSquare
    )
) {

    override fun createShape(location: DoubleVector, width: Double): SvgSlimShape {
        throw IllegalStateException("Not applicable")
    }

    companion object {
        // equilateral triangle
        private val SIDE_TO_HEIGHT_RATIO = sin(PI / 3)
        private const val VERTICAL_OFFSET_RATIO = 1.0 / 12

        private fun createTriangleShape(
            location: DoubleVector,
            size: Double,
            pointingUp: Boolean,
            inscribedInSquare: Boolean
        ): SvgSlimShape {
            val half = size / 2
            val height = if (inscribedInSquare)
                size
            else
                SIDE_TO_HEIGHT_RATIO * size

            val vOffset = if (inscribedInSquare)
                0.0
            else
                height * VERTICAL_OFFSET_RATIO

            val x: List<Double>
            val y: List<Double>
            var dy = (size - height) / 2
            if (pointingUp) {
                dy -= vOffset
                x = listOf(half, size, 0.0)
                y = listOf(0.0 + dy, height + dy, height + dy)
            } else {
                dy += vOffset
                x = listOf(0.0, size, half)
                y = listOf(0.0 + dy, 0.0 + dy, height + dy)
            }

            val ox = location.x - half
            val oy = location.y - half

            val pathData = GlyphUtil.buildPathData(
                x.map { it + ox },
                y.map { it + oy }
            )
            return SvgSlimElements.path(pathData)
        }
    }
}
