/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.geometry

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

class Vector(val x: Int, val y: Int) {

    companion object {
        val ZERO = Vector(0, 0)
    }

    fun add(v: Vector): Vector {
        return Vector(x + v.x, y + v.y)
    }

    fun sub(v: Vector): Vector {
        return add(v.negate())
    }

    fun negate(): Vector {
        return Vector(-x, -y)
    }

    fun max(v: Vector): Vector {
        return Vector(max(x, v.x), max(y, v.y))
    }

    fun min(v: Vector): Vector {
        return Vector(min(x, v.x), min(y, v.y))
    }

    fun mul(i: Int): Vector {
        return Vector(x * i, y * i)
    }

    operator fun div(i: Int): Vector {
        return Vector(x / i, y / i)
    }

    fun dotProduct(v: Vector): Int {
        return x * v.x + y * v.y
    }

    fun length(): Double {
        return sqrt((x * x + y * y).toDouble())
    }

    fun toDoubleVector(): DoubleVector {
        return DoubleVector(x.toDouble(), y.toDouble())
    }

    fun abs(): Vector {
        return Vector(abs(x), abs(y))
    }

    fun isParallel(to: Vector): Boolean {
        return x * to.y - to.x * y == 0
    }

    fun orthogonal(): Vector {
        return Vector(-y, x)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Vector) return false

        val otherVector = other as Vector?
        return x == otherVector!!.x && y == otherVector.y
    }

    override fun hashCode(): Int {
        return x * 31 + y
    }

    override fun toString(): String {
        return "($x, $y)"
    }

//    operator fun get(axis: Axis): Int {
//        val value: Int
//        when (axis) {
//            X -> value = x
//            Y -> value = y
//            else -> throw UnsupportedOperationException("unknown axis: $axis")
//        }
//        return value
//    }
}
