package jetbrains.livemap.projections

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleRectangles
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.projectionGeometry.GeoUtils.calculateQuadKeys
import jetbrains.datalore.base.projectionGeometry.GeoUtils.calculateTileKeys
import jetbrains.datalore.base.projectionGeometry.GeoUtils.getTileRect
import jetbrains.datalore.base.projectionGeometry.MultiPolygon
import jetbrains.datalore.base.projectionGeometry.Polygon
import jetbrains.datalore.base.projectionGeometry.QuadKey
import jetbrains.datalore.base.projectionGeometry.Ring
import jetbrains.livemap.projections.ProjectionType.*
import kotlin.math.PI
import kotlin.math.atan2

object ProjectionUtil {
    const val TILE_PIXEL_SIZE = 256.0
    const val SAMPLING_EPSILON = 0.001

    private val PROJECTION_MAP: Map<ProjectionType, GeoProjection> = hashMapOf (
        GEOGRAPHIC to GeographicProjection(),
        MERCATOR to MercatorProjection(),
        AZIMUTHAL_EQUAL_AREA to AzimuthalEqualAreaProjection(),
        AZIMUTHAL_EQUIDISTANT to AzimuthalEquidistantProjection(),
        CONIC_CONFORMAL to ConicConformalProjection(0.0, PI / 3),
        CONIC_EQUAL_AREA to ConicEqualAreaProjection(0.0, PI / 3)
    )

    internal fun createGeoProjection(projectionType: ProjectionType): GeoProjection {
        return PROJECTION_MAP[projectionType] ?: error("Unknown projection type: $projectionType")
    }

    fun createMapProjection(projectionType: ProjectionType, mapRect: DoubleRectangle): MapProjection {
        return MapProjectionBuilder(createGeoProjection(projectionType), mapRect)
            .reverseY()
            .create()
    }

    fun convertCellKeyToQuadKeys(mapProjection: MapProjection, cellKey: CellKey): Set<QuadKey> {
        val cellRect = getTileRect(mapProjection.mapRect, cellKey.key)
        val geoRect = transformBBox(cellRect, mapProjection::invert)
        return calculateQuadKeys(geoRect, cellKey.length)
    }

    internal fun calculateCellKeys(mapRect: DoubleRectangle, rect: DoubleRectangle, zoom: Int): Set<CellKey> {
        return calculateTileKeys(mapRect, rect, zoom, ::CellKey)
    }

    internal fun calculateAngle(coord1: DoubleVector, coord2: DoubleVector): Double {
        return atan2(
            coord1.y - coord2.y,
            coord2.x - coord1.x
        )
    }

    private fun rectToPolygon(rect: DoubleRectangle): List<DoubleVector> {
        val points = ArrayList<DoubleVector>()
        points.add(rect.origin)
        points.add(rect.origin.add(DoubleVector(rect.width, 0.0)))
        points.add(rect.origin.add(rect.dimension))
        points.add(rect.origin.add(DoubleVector(0.0, rect.height)))
        points.add(rect.origin)
        return points
    }

    fun square(projection: Projection<Double>): Projection<DoubleVector> {
        return tuple(projection, projection)
    }

    internal fun tuple(xProjection: Projection<Double>, yProjection: Projection<Double>): Projection<DoubleVector> {
        return object : Projection<DoubleVector> {
            override fun project(v: DoubleVector): DoubleVector {
                return DoubleVector(
                    xProjection.project(v.x),
                    yProjection.project(v.y)
                )
            }

            override fun invert(v: DoubleVector): DoubleVector {
                return DoubleVector(
                    xProjection.invert(v.x),
                    yProjection.invert(v.y)
                )
            }
        }
    }

    fun <T> composite(projection1: Projection<T>, projection2: Projection<T>): Projection<T> {
        return object : Projection<T> {
            override fun project(v: T): T {
                return projection2.project(projection1.project(v))
            }

            override fun invert(v: T): T {
                return projection1.invert(projection2.invert(v))
            }
        }
    }

    fun zoom(zoom: Int): Projection<Double> {
        return scale((1 shl zoom).toDouble())
    }

    internal fun linear(offset: Double, scale: Double): Projection<Double> {
        return composite(offset(offset), scale(scale))
    }

    internal fun offset(offset: Double): Projection<Double> {
        return object : Projection<Double> {
            override fun project(v: Double): Double {
                return v - offset
            }

            override fun invert(v: Double): Double {
                return v + offset
            }
        }
    }

    internal fun scale(scale: Double): Projection<Double> {
        return object : Projection<Double> {
            override fun project(v: Double): Double {
                return v * scale
            }

            override fun invert(v: Double): Double {
                return v / scale
            }
        }
    }

    fun transformBBox(bbox: DoubleRectangle, transform: (DoubleVector) -> DoubleVector): DoubleRectangle {
        return DoubleRectangles.boundingBox(transformRing(rectToPolygon(bbox), transform, SAMPLING_EPSILON))
    }

    fun transformMultipolygon(
        multiPolygon: MultiPolygon,
        transform: (DoubleVector) -> DoubleVector
    ): MultiPolygon {
        val xyMultipolygon = ArrayList<Polygon>(multiPolygon.size)
        multiPolygon.forEach { xyMultipolygon.add(transformPolygon(it, transform, SAMPLING_EPSILON)) }
        return MultiPolygon(xyMultipolygon)
    }

    private fun transformPolygon(
        polygon: Polygon,
        transform: (DoubleVector) -> DoubleVector,
        epsilon: Double
    ): Polygon {
        val xyPolygon = ArrayList<Ring>(polygon.size)
        polygon.forEach { ring -> xyPolygon.add(Ring(transformRing(ring, transform, epsilon))) }
        return Polygon(xyPolygon)
    }

    private fun transformRing(
        path: List<DoubleVector>,
        transform: (DoubleVector) -> DoubleVector,
        epsilon: Double
    ): List<DoubleVector> {
        return AdaptiveResampling(transform, epsilon).resample(path)
    }

    fun transform(multiPolygon: MultiPolygon, transform: (DoubleVector) -> DoubleVector): MultiPolygon {
        val xyMultipolygon = ArrayList<Polygon>(multiPolygon.size)
        multiPolygon.forEach { polygon -> xyMultipolygon.add(transform(polygon, transform, SAMPLING_EPSILON)) }
        return MultiPolygon(xyMultipolygon)
    }

    private fun transform(polygon: Polygon, transform: (DoubleVector) -> DoubleVector, epsilon: Double): Polygon {
        val xyPolygon = ArrayList<Ring>(polygon.size)
        polygon.forEach { ring -> xyPolygon.add(Ring(transform(ring, transform, epsilon))) }
        return Polygon(xyPolygon)
    }

    private fun transform(
        path: List<DoubleVector>,
        transform: (DoubleVector) -> DoubleVector,
        epsilon: Double
    ): List<DoubleVector> {
        val res = ArrayList<DoubleVector>(path.size)
        for (p in path) {
            res.add(transform(p))
        }
        return res
    }

    fun safeDoubleVector(x: Double, y: Double): DoubleVector {
        return if (x.isNaN() || y.isNaN()) {
            error("Value for DoubleVector isNaN x = $x and y = $y")
        } else {
            DoubleVector(x, y)
        }
    }
}