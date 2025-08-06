/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.typedGeometry

import kotlin.math.sqrt

data class Vec<out TypeT> (
    val x: Double,
    val y: Double
) {
    constructor(x: Number, y: Number) : this(x.toDouble(), y.toDouble())

    companion object {
        val ZERO: Vec<Nothing> = Vec(0.0, 0.0) // Ensure that the 'xxx is not a function' error does not occur in JavaScript.
    }
}

fun <T> explicitVec(x: Double, y: Double): Vec<T> = Vec(x, y)
fun <T> explicitVec(x: Int, y: Int): Vec<T> = Vec(x.toDouble(), y.toDouble())
fun <T> newVec(x: Scalar<T>, y: Scalar<T>): Vec<T> = Vec(x.value, y.value)


val <TypeT> Vec<TypeT>.scalarX get(): Scalar<TypeT> = Scalar(x)
val <TypeT> Vec<TypeT>.scalarY get(): Scalar<TypeT> = Scalar(y)

operator fun <TypeT> Vec<TypeT>.plus(other: Vec<TypeT>): Vec<TypeT> = Vec(x + other.x, y + other.y)
operator fun <TypeT> Vec<TypeT>.minus(other: Vec<TypeT>): Vec<TypeT> = Vec(x - other.x, y - other.y)
operator fun <TypeT> Vec<TypeT>.times(other: Vec<TypeT>): Vec<TypeT> = Vec(x * other.x, y * other.y)
operator fun <TypeT> Vec<TypeT>.div(other: Vec<TypeT>): Vec<TypeT> = Vec(x / other.x, y / other.y)

operator fun <TypeT> Vec<TypeT>.times(scale: Double): Vec<TypeT> = Vec(x * scale, y * scale)
operator fun <TypeT> Vec<TypeT>.div(scale: Double): Vec<TypeT> = Vec(x / scale, y / scale)
operator fun <TypeT> Vec<TypeT>.unaryMinus(): Vec<TypeT> = Vec(-x, -y)
fun <TypeT> Vec<TypeT>.min(other: Vec<TypeT>): Vec<TypeT> = Vec(kotlin.math.min(x, other.x), kotlin.math.min(y, other.y))
fun <TypeT> Vec<TypeT>.max(other: Vec<TypeT>): Vec<TypeT> = Vec(kotlin.math.max(x, other.x), kotlin.math.max(y, other.y))

fun <TypeT> Vec<TypeT>.transform(
    newX: (Scalar<TypeT>) -> Scalar<TypeT> = { it },
    newY: (Scalar<TypeT>) -> Scalar<TypeT> = { it }
) = Vec<TypeT>(newX(scalarX).value, newY(scalarY).value)

val <T> Vec<T>.length get() = Scalar<T>(sqrt(x * x + y * y))
