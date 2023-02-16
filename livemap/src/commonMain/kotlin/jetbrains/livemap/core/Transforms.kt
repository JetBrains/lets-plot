/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.core

import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.datalore.base.typedGeometry.explicitVec
import kotlin.math.pow

object Transforms {
    fun <InT, OutT> zoom(factor: () -> Int): Transform<Vec<InT>, Vec<OutT>> {
        return tuple(
            scale { 2.0.pow(factor()) },
            scale { 2.0.pow(factor()) }
        )
    }

    internal fun <InT, OutT> tuple(
        xTransform: Transform<Double, Double>,
        yTransform: Transform<Double, Double>
    ): Transform<Vec<InT>, Vec<OutT>> {
        return object : Transform<Vec<InT>, Vec<OutT>> {
            override fun apply(v: Vec<InT>): Vec<OutT> = explicitVec(xTransform.apply(v.x), yTransform.apply(v.y))
            override fun invert(v: Vec<OutT>): Vec<InT> = explicitVec(xTransform.invert(v.x), yTransform.invert(v.y))
        }
    }

    internal fun scale(scale: () -> Double): Transform<Double, Double> {
        return object : Transform<Double, Double> {
            override fun apply(v: Double): Double = v * scale()
            override fun invert(v: Double): Double = v / scale()
        }
    }
}