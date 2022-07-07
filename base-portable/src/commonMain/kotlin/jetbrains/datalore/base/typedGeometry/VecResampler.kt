/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.typedGeometry

import jetbrains.datalore.base.algorithms.AdaptiveResampler


class VecResampler<InT, OutT>(
    private val transform: (Vec<InT>) -> Vec<OutT>?,
    precision: Double
) {
    private val resampler = AdaptiveResampler(
        this::tranformWrapper,
        VEC_DELEGATE,
        precision
    )

    private fun tranformWrapper(v: Vec<*>): Vec<*>? {
        @Suppress("UNCHECKED_CAST")
        return transform(v as Vec<InT>)
    }

    fun resample(points: List<Vec<InT>>): List<Vec<OutT>> {
        @Suppress("UNCHECKED_CAST")
        return resampler.resample(points) as List<Vec<OutT>>
    }

    fun resample(p1: Vec<InT>, p2: Vec<InT>): List<Vec<OutT>> {
        @Suppress("UNCHECKED_CAST")
        return resampler.resample(p1, p2) as List<Vec<OutT>>
    }

    private companion object {
        val VEC_DELEGATE = object : AdaptiveResampler.PointDelegate<Vec<*>> {
            override fun x(obj: Vec<*>): Double = obj.x
            override fun y(obj: Vec<*>): Double = obj.y
            override fun newObj(x: Double, y: Double): Vec<*> = Vec<Generic>(x, y)
        }
    }
}
