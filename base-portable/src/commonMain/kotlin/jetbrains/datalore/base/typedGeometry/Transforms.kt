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
    ): Rect<OutT>? {
        return bbox
            .let(::rectToPolygon)
            .let { transformPoints(it, transform, resamplingPrecision) }
            .boundingBox()
    }

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

    private fun <TypeT> rectToPolygon(rect: Rect<TypeT>): List<Vec<TypeT>> {
        val points = ArrayList<Vec<TypeT>>()
        points.add(rect.origin)
        points.add(rect.origin.transform(newX = { it + rect.scalarWidth }))
        points.add(rect.origin + rect.dimension)
        points.add(rect.origin.transform(newY = { it + rect.scalarHeight }))
        points.add(rect.origin)
        return points
    }
}
