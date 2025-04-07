/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.geometry

import kotlin.math.cos
import kotlin.math.sin

/*
    Represents the 2D affine transformation matrix in the form:
    | m00 m01 m02 |
    | m10 m11 m12 |
    | 0   0   1   |

    or

    | scaleX  skewX  translateX |
    | skewY   scaleY translateY |
    | 0       0      1         |
 */
class AffineTransform(
    private val m00: Double, // scaleX
    private val m10: Double, // skewY
    private val m01: Double, // skewX
    private val m11: Double, // scaleY
    private val m02: Double, // translateX
    private val m12: Double // translateY
){

    fun transform(x: Double, y: Double): DoubleVector {
        return DoubleVector(
            x = m00 * x + m01 * y + m02,
            y = m10 * x + m11 * y + m12
        )
    }

    fun concat(other: AffineTransform): AffineTransform {
        return AffineTransform(
            m00 * other.m00 + m01 * other.m10,
            m10 * other.m00 + m11 * other.m10,
            m00 * other.m01 + m01 * other.m11,
            m10 * other.m01 + m11 * other.m11,
            m00 * other.m02 + m01 * other.m12 + m02,
            m10 * other.m02 + m11 * other.m12 + m12
        )
    }

    companion object {
        val IDENTITY = AffineTransform(1.0, 0.0, 0.0, 1.0, 0.0, 0.0)

        fun makeScale(scaleX: Double, scaleY: Double): AffineTransform {
            return AffineTransform(scaleX, 0.0, 0.0, scaleY, 0.0, 0.0)
        }

        fun makeTranslation(translateX: Double, translateY: Double): AffineTransform {
            return AffineTransform(1.0, 0.0, 0.0, 1.0, translateX, translateY)
        }

        fun makeRotation(angle: Double): AffineTransform {
            val cos = cos(angle)
            val sin = sin(angle)
            return AffineTransform(cos, -sin, sin, cos, 0.0, 0.0)
        }

        fun makeRotation(angle: Double, centerX: Double, centerY: Double): AffineTransform {
            val cos = cos(angle)
            val sin = sin(angle)
            return AffineTransform(
                cos, -sin,
                sin, cos,
                centerX * (1 - cos) + centerY * sin,
                centerY * (1 - cos) - centerX * sin
            )
        }

        // Creates a transform from the matrix elements
        // | sx  ry  tx |
        // | rx  sy  ty |
        fun makeTransform(sx: Double, ry: Double, rx: Double, sy: Double, tx: Double, ty: Double): AffineTransform {
            return AffineTransform(sx, ry, rx, sy, tx, ty)
        }
    }
}