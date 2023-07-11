/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.typedGeometry

import org.jetbrains.letsPlot.commons.intern.typedGeometry.algorithms.AdaptiveResampler


class VecResampler<InT, OutT>(
    private val transform: (Vec<InT>) -> Vec<OutT>?,
    precision: Double
) {
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

    private companion object {
        val VEC_ADAPTER = object : AdaptiveResampler.DataAdapter<Vec<*>> {
            override fun x(p: Vec<*>): Double = p.x
            override fun y(p: Vec<*>): Double = p.y
            override fun create(x: Double, y: Double): Vec<*> = Vec<Untyped>(x, y)
        }
    }
}
