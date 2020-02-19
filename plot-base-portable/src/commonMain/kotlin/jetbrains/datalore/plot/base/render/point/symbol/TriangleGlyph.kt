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

internal class TriangleGlyph @JvmOverloads constructor(location: DoubleVector, size: Double, pointingUp: Boolean, inscribedInSquare: Boolean = false) : SingletonGlyph(
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
                location: DoubleVector, size: Double, pointingUp: Boolean, inscribedInSquare: Boolean): SvgSlimShape {
            val half = size / 2
            val height = if (inscribedInSquare)
                size
            else
                SIDE_TO_HEIGHT_RATIO * size

            val vOffset = if (inscribedInSquare)
                0.0
            else
                height * VERTICAL_OFFSET_RATIO

            val x: DoubleArray
            val y: DoubleArray
            var dy = (size - height) / 2
            if (pointingUp) {
                dy -= vOffset
                x = doubleArrayOf(half, size, 0.0)
                y = doubleArrayOf(0.0 + dy, height + dy, height + dy)
            } else {
                dy += vOffset
                x = doubleArrayOf(0.0, size, half)
                y = doubleArrayOf(0.0 + dy, 0.0 + dy, height + dy)
            }

            val ox = location.x - half
            val oy = location.y - half
            for (i in 0..2) {
                x[i] = ox + x[i]
                y[i] = oy + y[i]
            }

            val pathData =
                GlyphUtil.buildPathData(
                    x.asList(),
                    y.asList()
                )
            return SvgSlimElements.path(pathData)
        }
    }
}
