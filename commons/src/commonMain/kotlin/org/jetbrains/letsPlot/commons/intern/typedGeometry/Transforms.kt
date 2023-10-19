/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.typedGeometry

import org.jetbrains.letsPlot.commons.intern.typedGeometry.VecResampler.Companion.resample

object Transforms {
    private const val RESAMPLING_PRECISION = 0.004

    fun <InT, OutT> transform(
        bbox: Rect<InT>,
        transform: (Vec<InT>) -> Vec<OutT>?,
        resamplingPrecision: Double? = RESAMPLING_PRECISION
    ): Rect<OutT>? = transform(bbox.toPolygon(), transform, resamplingPrecision).bbox

    fun <InT, OutT> transform(
        multiPolygon: MultiPolygon<InT>,
        transform: (Vec<InT>) -> Vec<OutT>?,
        resamplingPrecision: Double? = RESAMPLING_PRECISION
    ): MultiPolygon<OutT> = MultiPolygon(multiPolygon.map { transform(it, transform, resamplingPrecision) })

    fun <InT, OutT> transform(
        polygon: Polygon<InT>,
        transform: (Vec<InT>) -> Vec<OutT>?,
        resamplingPrecision: Double?
    ): Polygon<OutT> = Polygon(polygon.map { Ring(transformPoints(it, transform, resamplingPrecision)) })

    fun <InT, OutT> transform(
        lineString: LineString<InT>,
        transform: (Vec<InT>) -> Vec<OutT>?,
        resamplingPrecision: Double?
    ): LineString<OutT> = LineString(transformPoints(lineString, transform, resamplingPrecision))

    fun <InT, OutT> transform(
        multiLineString: MultiLineString<InT>,
        transform: (Vec<InT>) -> Vec<OutT>?,
        resamplingPrecision: Double?
    ): MultiLineString<OutT> = MultiLineString(multiLineString.map { transform(it, transform, resamplingPrecision) } )

    fun <InT, OutT> transformPoints(
        path: List<Vec<InT>>,
        transform: (Vec<InT>) -> Vec<OutT>?,
        resamplingPrecision: Double?
    ): List<Vec<OutT>> = when(resamplingPrecision) {
        null -> path.mapNotNull(transform)
        else -> resample(path, resamplingPrecision, transform)
    }
}
