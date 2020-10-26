/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.typedGeometry

data class Vec<TypeT> (
    val x: Double,
    val y: Double
) {
    constructor(x: Int, y: Int) : this(x.toDouble(), y.toDouble())
}

fun <T> explicitVec(x: Double, y: Double): Vec<T> = Vec(x, y)
fun <T> newVec(x: Scalar<T>, y: Scalar<T>): Vec<T> = Vec(x.value, y.value)
