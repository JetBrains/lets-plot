/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.typedGeometry

import org.jetbrains.letsPlot.commons.intern.typedGeometry.algorithms.AdaptiveResampler


class VecResampler<InT, OutT> private constructor(
    private val transform: (Vec<InT>) -> Vec<OutT>?,
    precision: Double
) {
    companion object {
        private val VEC_ADAPTER = object : AdaptiveResampler.DataAdapter<Vec<*>> {
            override fun x(p: Vec<*>): Double = p.x
            override fun y(p: Vec<*>): Double = p.y
            override fun create(x: Double, y: Double): Vec<*> = Vec<Untyped>(x, y)
        }

        fun <InT, OutT> resample(
            points: List<Vec<InT>>,
            precision: Double,
            transform: (Vec<InT>) -> Vec<OutT>?
        ): List<Vec<OutT>> {
            return VecResampler(transform, precision).resample(points)
        }

        fun <InT, OutT> resample(
            p1: Vec<InT>,
            p2: Vec<InT>,
            precision: Double,
            transform: (Vec<InT>) -> Vec<OutT>?
        ): List<Vec<OutT>> {
            return VecResampler(transform, precision).resample(p1, p2)
        }
    }

    private val resampler = AdaptiveResampler.generic(this::transformWrapper, precision, VEC_ADAPTER)

    private fun transformWrapper(v: Vec<*>): Vec<*>? {
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
}
