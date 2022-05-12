/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.typedGeometry

// TODO: change to <out Vec> for proper ZERO const. Now it causes "xxx is not a function" in JS
data class Vec<TypeT> (
    val x: Double,
    val y: Double
) {
    constructor(x: Int, y: Int) : this(x.toDouble(), y.toDouble())
    //val ZERO: Vec<Nothing> = Vec<Nothing>(0.0, 0.0) // TODO: proper ZERO const
}

fun <T> explicitVec(x: Double, y: Double): Vec<T> = Vec(x, y)
fun <T> explicitVec(x: Int, y: Int): Vec<T> = Vec(x.toDouble(), y.toDouble())
fun <T> newVec(x: Scalar<T>, y: Scalar<T>): Vec<T> = Vec(x.value, y.value)
