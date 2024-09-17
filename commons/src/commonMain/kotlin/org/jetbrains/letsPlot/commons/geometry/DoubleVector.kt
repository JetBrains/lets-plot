/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.geometry

import kotlin.math.*

class DoubleVector(val x: Double, val y: Double) {
    constructor(x: Number, y: Number) : this(x.toDouble(), y.toDouble())
    constructor(pair: Pair<Number, Number>) : this(pair.first, pair.second)

    operator fun component1(): Double = x
    operator fun component2(): Double = y

    val isFinite: Boolean get() = x.isFinite() && y.isFinite()

    fun add(v: DoubleVector): DoubleVector {
        return DoubleVector(x + v.x, y + v.y)
    }

    fun subtract(v: DoubleVector): DoubleVector {
        return DoubleVector(x - v.x, y - v.y)
    }

    fun max(v: DoubleVector): DoubleVector {
        return DoubleVector(max(x, v.x), max(y, v.y))
    }

    fun min(v: DoubleVector): DoubleVector {
        return DoubleVector(min(x, v.x), min(y, v.y))
    }

    fun mul(value: Double): DoubleVector {
        return DoubleVector(x * value, y * value)
    }

    fun dotProduct(v: DoubleVector): Double {
        return x * v.x + y * v.y
    }

    fun negate(): DoubleVector {
        return DoubleVector(-x, -y)
    }

    fun orthogonal(): DoubleVector {
        return DoubleVector(-y, x)
    }

    fun length(): Double {
        return sqrt(x * x + y * y)
    }

    fun normalize(): DoubleVector {
        return mul(1 / length())
    }

    fun rotateAround(center: DoubleVector, phi: Double): DoubleVector {
        return subtract(center).rotate(phi).add(center)
    }

    fun rotate(phi: Double): DoubleVector {
        val x = this.x * cos(phi) - this.y * sin(phi)
        val y = this.x * sin(phi) + this.y * cos(phi)
        return DoubleVector(x, y)
    }

    fun flip(): DoubleVector {
        return DoubleVector(y, x)
    }

    fun flipIf(flipped: Boolean): DoubleVector {
        return if (flipped) flip() else this
    }

    override fun equals(other: Any?): Boolean {
        if (other !is DoubleVector) {
            return false
        }
        val v = other as DoubleVector?
        return v!!.x == x && v.y == y
    }

    override fun hashCode(): Int {
        return x.hashCode() + 31 * y.hashCode()
    }

    override fun toString(): String {
        return "($x, $y)"
    }

    companion object {
        val ZERO = DoubleVector(0.0, 0.0)
    }
}