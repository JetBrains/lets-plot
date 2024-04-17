/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.util

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Untyped
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Vec

interface VectorAdapter<T> {
    fun x(p: T): Double
    fun y(p: T): Double
    fun create(x: Double, y: Double): T
    fun create(x: Number, y: Number): T = create(x.toDouble(), y.toDouble())

    val T.x get() = x(this)
    val T.y get() = y(this)

    companion object {
        val DOUBLE_VECTOR_ADAPTER = object : VectorAdapter<DoubleVector> {
            override fun x(p: DoubleVector) = p.x
            override fun y(p: DoubleVector) = p.y
            override fun create(x: Double, y: Double) = DoubleVector(x, y)
        }

        val VEC_ADAPTER = object : VectorAdapter<Vec<*>> {
            override fun x(p: Vec<*>): Double = p.x
            override fun y(p: Vec<*>): Double = p.y
            override fun create(x: Double, y: Double): Vec<*> = Vec<Untyped>(x, y)
        }
    }
}
