/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.typedGeometry

import kotlin.jvm.JvmInline

@JvmInline
value class Scalar<out T>(
    val value: Double
) : Comparable<Scalar<@UnsafeVariance T>> {
    constructor(value: Number) : this(value.toDouble())

    override fun compareTo(other: Scalar<@UnsafeVariance T>): Int {
        return value.compareTo(other.value)
    }

    companion object {
        val ZERO: Scalar<Nothing> = Scalar(0.0)
    }
}


operator fun <T> Scalar<T>.plus(other: Scalar<T>): Scalar<T> = Scalar(value + other.value)
operator fun <T> Scalar<T>.minus(other: Scalar<T>): Scalar<T> = Scalar(value - other.value)
operator fun <T> Scalar<T>.times(other: Scalar<T>): Scalar<T> = Scalar(value * other.value)

operator fun <T> Scalar<T>.div(other: Scalar<T>): Scalar<T> = Scalar(value / other.value)
operator fun <T> Scalar<T>.div(other: Number): Scalar<T> = Scalar(value / other.toDouble())
operator fun <T> Scalar<T>.times(other: Number): Scalar<T> = Scalar(value * other.toDouble())
operator fun <T> Scalar<T>.unaryMinus(): Scalar<T> = Scalar(-value)

operator fun <T> Scalar<T>.compareTo(i: Int) = value.compareTo(i)