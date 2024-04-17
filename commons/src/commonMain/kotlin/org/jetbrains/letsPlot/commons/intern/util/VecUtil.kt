/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.util

import org.jetbrains.letsPlot.commons.intern.typedGeometry.Scalar
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Vec
import org.jetbrains.letsPlot.commons.intern.typedGeometry.algorithms.AdaptiveResampler
import org.jetbrains.letsPlot.commons.intern.util.VectorAdapter.Companion.VEC_ADAPTER

object VecUtil {
    fun <T> padLineString(lineString: List<Vec<T>>, startPadding: Double, endPadding: Double): List<Vec<T>> {
        val paddingHelper = PaddingHelper(VEC_ADAPTER)
        val lineStringAfterPadding = paddingHelper.padLineString(
            lineString = lineString,
            startPadding = startPadding,
            endPadding = endPadding
        )

        @Suppress("UNCHECKED_CAST")
        return lineStringAfterPadding as List<Vec<T>>
    }

    fun <T> createArrowHeadGeometry(
        geometry: List<Vec<T>>,
        angle: Double,
        arrowLength: Double,
        onStart: Boolean,
        onEnd: Boolean,
        closed: Boolean,
        minTailLength: Scalar<T>,
        minHeadLength: Scalar<T>,
    ): Pair<List<Vec<T>>, List<Vec<T>>> {
        val arrowHelper = ArrowSupport.ArrowHelper(VEC_ADAPTER)
        val heads = arrowHelper.createArrowHeads(
            lineString = geometry,
            angle = angle,
            arrowLength = arrowLength,
            onStart = onStart,
            onEnd = onEnd,
            closed = closed,
            minTailLength = minTailLength.value,
            minHeadLength = minHeadLength.value
        )

        @Suppress("UNCHECKED_CAST")
        return heads as Pair<List<Vec<T>>, List<Vec<T>>>
    }

    fun <InT, OutT> resample(points: List<Vec<InT>>, precision: Double, transform: (Vec<InT>) -> Vec<OutT>?): List<Vec<OutT>> {
        val resampler = AdaptiveResampler.generic(precision, VEC_ADAPTER) {
            @Suppress("UNCHECKED_CAST")
            transform(it as Vec<InT>)
        }

        @Suppress("UNCHECKED_CAST")
        return resampler.resample(points) as List<Vec<OutT>>
    }

    fun <InT, OutT> resample(p1: Vec<InT>, p2: Vec<InT>, precision: Double, transform: (Vec<InT>) -> Vec<OutT>?,): List<Vec<OutT>> {
        val resampler = AdaptiveResampler.generic(precision, VEC_ADAPTER) {
            @Suppress("UNCHECKED_CAST")
            transform(it as Vec<InT>)
        }

        @Suppress("UNCHECKED_CAST")
        return resampler.resample(p1, p2) as List<Vec<OutT>>
    }
}
