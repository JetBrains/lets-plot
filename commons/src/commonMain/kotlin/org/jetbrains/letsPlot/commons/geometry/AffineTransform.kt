/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.geometry

import kotlin.math.cos
import kotlin.math.sin

/*
    Represents the 2D affine transformation matrix in the form:
    | m00 / sx | m01 / rx | m02 / tx |
    | -------- | -------- | -------- |
    | m10 / ry | m11 / sy | m12 / ty |
    | -------- | -------- | -------- |
    |     0    |     0    |     1    |

 */
class AffineTransform(
    val m00: Double, // sx
    val m10: Double, // ry
    val m01: Double, // rx
    val m11: Double, // sy
    val m02: Double, // tx
    val m12: Double // ty
) {

    val isIdentity: Boolean = m00 == 1.0 && m10 == 0.0 && m01 == 0.0 && m11 == 1.0 && m02 == 0.0 && m12 == 0.0

    // synonyms
    val sx: Double get() = m00
    val sy: Double get() = m11
    val rx: Double get() = m01
    val ry: Double get() = m10
    val tx: Double get() = m02
    val ty: Double get() = m12

    fun transform(p: DoubleVector): DoubleVector {
        if (isIdentity) {
            return p
        }
        return transform(p.x, p.y)
    }

    // If identity, return the same list
    fun transform(p: List<DoubleVector>): List<DoubleVector> {
        if (isIdentity) {
            return p
        }
        return p.map { transform(it) }
    }

    fun transform(r: DoubleRectangle): DoubleRectangle {
        if (isIdentity) {
            return r
        }

        val lt = transform(r.left, r.top)
        val rt = transform(r.right, r.top)
        val rb = transform(r.right, r.bottom)
        val lb = transform(r.left, r.bottom)

        val xs = listOf(lt.x, rt.x, rb.x, lb.x)
        val ys = listOf(lt.y, rt.y, rb.y, lb.y)

        return DoubleRectangle.LTRB(xs.min(), ys.min(), xs.max(), ys.max())
    }

    fun transform(x: Number, y: Number): DoubleVector {
        if (isIdentity) {
            return DoubleVector(x.toDouble(), y.toDouble())
        }

        return DoubleVector(
            x = m00 * x.toDouble() + m01 * y.toDouble() + m02,
            y = m10 * x.toDouble() + m11 * y.toDouble() + m12
        )
    }

    fun concat(other: AffineTransform): AffineTransform {
        return AffineTransform(
            m00 = m00 * other.m00 + m01 * other.m10,
            m10 = m10 * other.m00 + m11 * other.m10,
            m01 = m00 * other.m01 + m01 * other.m11,
            m11 = m10 * other.m01 + m11 * other.m11,
            m02 = m00 * other.m02 + m01 * other.m12 + m02,
            m12 = m10 * other.m02 + m11 * other.m12 + m12
        )
    }

    fun inverse(): AffineTransform? {
        val det = m00 * m11 - m01 * m10
        if (det == 0.0) {
            return null
        }
        return AffineTransform(
            m00 = m11 / det,
            m10 = -m10 / det,
            m01 = -m01 / det,
            m11 = m00 / det,
            m02 = (m01 * m12 - m02 * m11) / det,
            m12 = (m02 * m10 - m00 * m12) / det
        )
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as AffineTransform

        if (m00 != other.m00) return false
        if (m11 != other.m11) return false
        if (m02 != other.m02) return false
        if (m12 != other.m12) return false
        if (m10 != other.m10) return false
        if (m01 != other.m01) return false

        return true
    }

    override fun hashCode(): Int {
        var result = m00.hashCode()
        result = 31 * result + m11.hashCode()
        result = 31 * result + m02.hashCode()
        result = 31 * result + m12.hashCode()
        result = 31 * result + m10.hashCode()
        result = 31 * result + m01.hashCode()
        return result
    }

    fun repr(): String {
        if (m00 == 1.0 && m10 == 0.0 && m01 == 0.0 && m11 == 1.0 && m02 == 0.0 && m12 == 0.0) {
            return "IDENTITY"
        }
        return """m00=$m00, m10=$m10, m01=$m01, m11=$m11, m02=$m02, m12=$m12"""
    }


    companion object {
        val IDENTITY = AffineTransform(1.0, 0.0, 0.0, 1.0, 0.0, 0.0)

        fun makeScale(sx: Number, sy: Number): AffineTransform {
            return makeTransform(sx = sx, sy = sy)
        }

        fun makeTranslation(tx: Number, ty: Number): AffineTransform {
            return makeTransform(tx = tx, ty = ty)
        }

        fun makeShear(rx: Number, ry: Number): AffineTransform {
            return makeTransform(ry = ry, rx = rx)
        }

        fun makeRotation(angle: Number, centerX: Number = 0, centerY: Number = 0): AffineTransform {
            // TODO: check is it really needed
            //val tolerance = (1.0f / (1 shl 12)).toDouble()
            val sin = sin(angle.toDouble())//.takeIf { abs(it) > tolerance } ?: 0.0
            val cos = cos(angle.toDouble())//.takeIf { abs(it) > tolerance } ?: 0.0
            return makeTransform(
                sx = cos,
                ry = sin,
                rx = -sin,
                sy = cos,
                tx = centerX.toDouble() * (1 - cos) + centerY.toDouble() * sin,
                ty = centerY.toDouble() * (1 - cos) - centerX.toDouble() * sin
            )
        }

        // Creates a transform from the matrix elements
        // | m00 / sx | m01 / rx | m02 / tx |
        // | -------- | -------- | -------- |
        // | m10 / ry | m11 / sy | m12 / ty |
        // | -------- | -------- | -------- |
        // |     0    |     0    |     1    |
        fun makeTransform(
            sx: Number = 1, // m00
            ry: Number = 0, // m10
            rx: Number = 0, // m01
            sy: Number = 1, // m11
            tx: Number = 0, // m02
            ty: Number = 0  // m12
        ): AffineTransform {
            return AffineTransform(
                m00 = sx.toDouble(),
                m10 = ry.toDouble(),
                m01 = rx.toDouble(),
                m11 = sy.toDouble(),
                m02 = tx.toDouble(),
                m12 = ty.toDouble()
            )
        }

        fun makeMatrix(
            m00: Number = 1, // sx
            m10: Number = 0, // ry
            m01: Number = 0, // rx
            m11: Number = 1, // sy
            m02: Number = 0, // tx
            m12: Number = 0  // ty
        ): AffineTransform {
            return AffineTransform(
                m00 = m00.toDouble(),
                m10 = m10.toDouble(),
                m01 = m01.toDouble(),
                m11 = m11.toDouble(),
                m02 = m02.toDouble(),
                m12 = m12.toDouble()
            )
        }

        fun makeTranslate(dx: Number, dy: Number): AffineTransform {
            return makeTranslation(tx = dx, ty = dy)
        }
    }
}