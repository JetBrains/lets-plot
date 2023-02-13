/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.typedGeometry

object Transforms {
    private const val RESAMPLING_PRECISION = 0.001

    fun <InT, OutT> transformBBox(
        bbox: Rect<InT>,
        transform: (Vec<InT>) -> Vec<OutT>?,
        resamplingPrecision: Double? = RESAMPLING_PRECISION
    ): Rect<OutT>? = transformPolygon(bbox.toPolygon(), transform, resamplingPrecision).bbox

    fun <InT, OutT> transformMultiPolygon(
        multiPolygon: MultiPolygon<InT>,
        transform: (Vec<InT>) -> Vec<OutT>?,
        resamplingPrecision: Double? = RESAMPLING_PRECISION
    ): MultiPolygon<OutT> = MultiPolygon(multiPolygon.map { transformPolygon(it, transform, resamplingPrecision) })

    private fun <InT, OutT> transformPolygon(
        polygon: Polygon<InT>,
        transform: (Vec<InT>) -> Vec<OutT>?,
        resamplingPrecision: Double?
    ): Polygon<OutT> = Polygon(polygon.map { Ring(transformPoints(it, transform, resamplingPrecision)) })

    private fun <InT, OutT> transformPoints(
        path: List<Vec<InT>>,
        transform: (Vec<InT>) -> Vec<OutT>?,
        resamplingPrecision: Double?
    ): List<Vec<OutT>> = when(resamplingPrecision) {
        null -> path.mapNotNull(transform)
        else -> VecResampler(transform, resamplingPrecision).resample(path)
    }
}
