/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.projections

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleRectangles
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.projectionGeometry.*
import jetbrains.datalore.base.spatial.GeoUtils
import jetbrains.livemap.projections.ProjectionType.*
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.pow

object ProjectionUtil {
    const val TILE_PIXEL_SIZE = 256.0
    const val SAMPLING_EPSILON = 0.001

    private val PROJECTION_MAP: Map<ProjectionType, GeoProjection> = hashMapOf(
        GEOGRAPHIC to GeographicProjection(),
        MERCATOR to MercatorProjection(),
        AZIMUTHAL_EQUAL_AREA to AzimuthalEqualAreaProjection(),
        AZIMUTHAL_EQUIDISTANT to AzimuthalEquidistantProjection(),
        CONIC_CONFORMAL to ConicConformalProjection(0.0, PI / 3),
        CONIC_EQUAL_AREA to ConicEqualAreaProjection(0.0, PI / 3)
    )

    private fun getTileCount(zoom: Int): Int {
        return 1 shl zoom
    }

    fun <T> calculateTileKeys(
        mapRect: Rect<*>,
        viewRect: DoubleRectangle,
        zoom: Int?,
        constructor: (String) -> T
    ): Set<T> {
        val tileKeys = HashSet<T>()
        val tileCount = getTileCount(zoom!!)

        val xmin = GeoUtils.calcTileNum(viewRect.left, mapRect.xRange(), tileCount)
        val xmax = GeoUtils.calcTileNum(viewRect.right, mapRect.xRange(), tileCount)
        val ymin = GeoUtils.calcTileNum(viewRect.top, mapRect.yRange(), tileCount)
        val ymax = GeoUtils.calcTileNum(viewRect.bottom, mapRect.yRange(), tileCount)

        for (x in xmin..xmax) {
            for (y in ymin..ymax) {
                tileKeys.add(constructor(GeoUtils.tileXYToTileID(x, y, zoom)))
            }
        }

        return tileKeys
    }

    internal fun createGeoProjection(projectionType: ProjectionType): GeoProjection {
        return PROJECTION_MAP[projectionType] ?: error("Unknown projection type: $projectionType")
    }

    fun createMapProjection(projectionType: ProjectionType, mapRect: WorldRectangle): MapProjection {
        return MapProjectionBuilder(createGeoProjection(projectionType), mapRect)
            .reverseY()
            .create()
    }

    internal fun calculateAngle(coord1: DoubleVector, coord2: DoubleVector): Double {
        return atan2(
            coord1.y - coord2.y,
            coord2.x - coord1.x
        )
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

    fun <InT, OutT> square(projection: Projection<Double, Double>): Projection<Vec<InT>, Vec<OutT>> {
        return tuple(projection, projection)
    }

    internal fun <InT, OutT> tuple(
        xProjection: Projection<Double, Double>,
        yProjection: Projection<Double, Double>
    ): Projection<Vec<InT>, Vec<OutT>> {
        return object : Projection<Vec<InT>, Vec<OutT>> {
            override fun project(v: Vec<InT>): Vec<OutT> {
                return explicitVec(
                    xProjection.project(v.x),
                    yProjection.project(v.y)
                )
            }

            override fun invert(v: Vec<OutT>): Vec<InT> {
                return explicitVec(
                    xProjection.invert(v.x),
                    yProjection.invert(v.y)
                )
            }
        }
    }

    fun <InT, InterT, OutT> composite(
        t1: Projection<InT, InterT>,
        t2: Projection<InterT, OutT>
    ): Projection<InT, OutT> {
        return object : Projection<InT, OutT> {
            override fun project(v: InT): OutT {
                return v.run(t1::project).run(t2::project)
            }

            override fun invert(v: OutT): InT {
                return v.run(t2::invert).run(t1::invert)
            }
        }
    }

    fun zoom(zoom: () -> Int): Projection<Double, Double> {
        return scale { (2.0.pow(zoom())) }
    }

    internal fun scale(scale: () -> Double): Projection<Double, Double> {
        return object : Projection<Double, Double> {
            override fun project(v: Double): Double {
                return v * scale()
            }

            override fun invert(v: Double): Double {
                return v / scale()
            }
        }
    }

    internal fun linear(offset: Double, scale: Double): Projection<Double, Double> {
        return composite(offset(offset), scale(scale))
    }

    internal fun offset(offset: Double): Projection<Double, Double> {
        return object : Projection<Double, Double> {
            override fun project(v: Double): Double {
                return v - offset
            }

            override fun invert(v: Double): Double {
                return v + offset
            }
        }
    }

    fun zoom(zoom: Int): Projection<Double, Double> {
        return zoom { zoom }
    }

    internal fun scale(scale: Double): Projection<Double, Double> {
        return scale { scale }
    }

    fun <InT, OutT> transformBBox(bbox: Rect<InT>, transform: (Vec<InT>) -> Vec<OutT>): Rect<OutT> {
        return bbox
            .let(::rectToPolygon)
            .let { transformRing(it, transform, SAMPLING_EPSILON) }
            .let { DoubleRectangles.boundingBox(it) }
    }

    fun <InT, OutT> transformMultiPolygon(
        multiPolygon: MultiPolygon<InT>,
        transform: (Vec<InT>) -> Vec<OutT>
    ): MultiPolygon<OutT> {
        val xyMultipolygon = ArrayList<Polygon<OutT>>(multiPolygon.size)
        multiPolygon.forEach { xyMultipolygon.add(transformPolygon(it, transform, SAMPLING_EPSILON)) }
        return MultiPolygon<OutT>(xyMultipolygon)
    }

    private fun <InT, OutT> transformPolygon(
        polygon: Polygon<InT>,
        transform: (Vec<InT>) -> Vec<OutT>,
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
        transform: (Vec<InT>) -> Vec<OutT>,
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
        epsilon: Double
    ): List<Vec<OutT>> {
        val res = ArrayList<Vec<OutT>>(path.size)
        for (p in path) {
            res.add(transform(p))
        }
        return res
    }

    fun <TypeT> safePoint(x: Double, y: Double): Vec<TypeT> {
        return if (x.isNaN() || y.isNaN()) {
            error("Value for DoubleVector isNaN x = $x and y = $y")
        } else {
            explicitVec(x, y)
        }
    }
}