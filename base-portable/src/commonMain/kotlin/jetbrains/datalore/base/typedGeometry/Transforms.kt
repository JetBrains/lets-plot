/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.typedGeometry

object Transforms {
    private const val SAMPLING_EPSILON = 0.001

    fun <InT, OutT> transformBBox(bbox: Rect<InT>, transform: (Vec<InT>) -> Vec<OutT>?): Rect<OutT>? {
        return bbox
            .let(::rectToPolygon)
            .let { transformRing(it, transform, SAMPLING_EPSILON) }
            .boundingBox()
    }

    fun <InT, OutT> transformMultiPolygon(
        multiPolygon: MultiPolygon<InT>,
        transform: (Vec<InT>) -> Vec<OutT>?
    ): MultiPolygon<OutT> {
        val xyMultipolygon = ArrayList<Polygon<OutT>>(multiPolygon.size)
        multiPolygon.forEach { xyMultipolygon.add(transformPolygon(it, transform, SAMPLING_EPSILON)) }
        return MultiPolygon<OutT>(xyMultipolygon)
    }

    private fun <InT, OutT> transformPolygon(
        polygon: Polygon<InT>,
        transform: (Vec<InT>) -> Vec<OutT>?,
        epsilon: Double
    ): Polygon<OutT> {
        val xyPolygon = ArrayList<Ring<OutT>>(polygon.size)
        polygon.forEach { ring ->
            xyPolygon.add(
                Ring<OutT>(
                    transformRing(
                        ring,
                        transform,
                        epsilon
                    )
                )
            )
        }
        return Polygon<OutT>(xyPolygon)
    }

    private fun <InT, OutT> transformRing(
        path: List<Vec<InT>>,
        transform: (Vec<InT>) -> Vec<OutT>?,
        epsilon: Double
    ): List<Vec<OutT>> {
        return AdaptiveResampling(transform, epsilon).resample(path)
    }

    fun <InT, OutT> transform(
        multiPolygon: MultiPolygon<InT>,
        transform: (Vec<InT>) -> Vec<OutT>
    ): MultiPolygon<OutT> {
        val xyMultipolygon = ArrayList<Polygon<OutT>>(multiPolygon.size)
        multiPolygon.forEach { polygon -> xyMultipolygon.add(transform(polygon, transform, SAMPLING_EPSILON)) }
        return MultiPolygon<OutT>(xyMultipolygon)
    }

    private fun <InT, OutT> transform(
        polygon: Polygon<InT>,
        transform: (Vec<InT>) -> Vec<OutT>,
        epsilon: Double
    ): Polygon<OutT> {
        val xyPolygon = ArrayList<Ring<OutT>>(polygon.size)
        polygon.forEach { ring ->
            xyPolygon.add(
                Ring<OutT>(
                    transform(
                        ring,
                        transform,
                        epsilon
                    )
                )
            )
        }
        return Polygon<OutT>(xyPolygon)
    }

    private fun <InT, OutT> transform(
        path: List<Vec<InT>>,
        transform: (Vec<InT>) -> Vec<OutT>,
        @Suppress("UNUSED_PARAMETER") epsilon: Double
    ): List<Vec<OutT>> {
        val res = ArrayList<Vec<OutT>>(path.size)
        for (p in path) {
            res.add(transform(p))
        }
        return res
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