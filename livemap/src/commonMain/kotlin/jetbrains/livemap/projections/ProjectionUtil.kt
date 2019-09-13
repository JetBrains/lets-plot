package jetbrains.livemap.projections

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleRectangles
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.projectionGeometry.*
import jetbrains.datalore.base.projectionGeometry.GeoUtils.calculateQuadKeys
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

    private fun getTileCount(zoom: Int): Int {
        return 1 shl zoom
    }

    fun <T> calculateTileKeys(mapRect: Typed.Rectangle<*>, viewRect: DoubleRectangle, zoom: Int?, constructor: (String) -> T): Set<T> {
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

    fun getTileRect(mapRect: WorldRectangle, tileKey: String): WorldRectangle {
        val origin = getTileOrigin(mapRect, tileKey)
        val dimension = mapRect.dimension.mul(1.0 / getTileCount(tileKey.length))

        return WorldRectangle(origin, dimension)
    }

    private fun getTileOrigin(mapRect: WorldRectangle, tileKey: String): WorldPoint {
        var left = mapRect.left
        var top = mapRect.top
        var width = mapRect.width
        var height = mapRect.height

        for (quadrant in tileKey) {
            width /= 2.0
            height /= 2.0

            if (quadrant == '1' || quadrant == '3') {
                left += width
            }
            if (quadrant == '2' || quadrant == '3') {
                top += height
            }
        }
        return WorldPoint(left, top)
    }

    internal fun createGeoProjection(projectionType: ProjectionType): GeoProjection {
        return PROJECTION_MAP[projectionType] ?: error("Unknown projection type: $projectionType")
    }

    fun createMapProjection(projectionType: ProjectionType, mapRect: WorldRectangle): MapProjection {
        return MapProjectionBuilder(createGeoProjection(projectionType), mapRect)
            .reverseY()
            .create()
    }

    fun convertCellKeyToQuadKeys(mapProjection: MapProjection, cellKey: CellKey): Set<QuadKey> {
        val cellRect = getTileRect(mapProjection.mapRect, cellKey.key)
        val geoRect = transformBBox(cellRect, mapProjection::invert)
        return calculateQuadKeys(geoRect, cellKey.length)
    }

    internal fun calculateCellKeys(mapRect: Typed.Rectangle<*>, rect: DoubleRectangle, zoom: Int): Set<CellKey> {
        return calculateTileKeys(mapRect, rect, zoom, ::CellKey)
    }

    internal fun calculateAngle(coord1: DoubleVector, coord2: DoubleVector): Double {
        return atan2(
            coord1.y - coord2.y,
            coord2.x - coord1.x
        )
    }

    private fun <ProjT> rectToPolygon(rect: Typed.Rectangle<ProjT>): List<Typed.Point<ProjT>> {
        val points = ArrayList<Typed.Point<ProjT>>()
        points.add(rect.origin)
        points.add(rect.origin.addX(rect.dimension))
        points.add(rect.origin.add(rect.dimension))
        points.add(rect.origin.addY(rect.dimension))
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

    internal fun <InT, OutT> tuple(xProjection: Projection<Double>, yProjection: Projection<Double>): Transform<Typed.Point<InT>, Typed.Point<OutT>> {
        return object : Transform<Typed.Point<InT>, Typed.Point<OutT>> {
            override fun project(v: Typed.Point<InT>): Typed.Point<OutT> {
                return Typed.Point(
                    xProjection.project(v.x),
                    yProjection.project(v.y)
                )
            }

            override fun invert(v: Typed.Point<OutT>): Typed.Point<InT> {
                return Typed.Point(
                    xProjection.invert(v.x),
                    yProjection.invert(v.y)
                )
            }
        }
    }

    fun <T> composite(proj1: Projection<T>, proj2: Projection<T>): Projection<T> {
        return object : Projection<T> {
            override fun project(v: T): T {
                return v.run(proj1::project).run(proj2::project)
            }

            override fun invert(v: T): T {
                return v.run(proj2::invert).run(proj1::invert)
            }
        }
    }

    fun <InT, InterT, OutT> composite(t1: Transform<InT, InterT>, t2: Transform<InterT, OutT>): Transform<InT, OutT> {
        return object : Transform<InT, OutT> {
            override fun project(v: InT): OutT {
                return v.run(t1::project).run(t2::project)
            }

            override fun invert(v: OutT): InT {
                return v.run(t2::invert).run(t1::invert)
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

    fun <InT, OutT> transformBBox(bbox: Typed.Rectangle<InT>, transform: (Typed.Point<InT>) -> Typed.Point<OutT>): Typed.Rectangle<OutT> {
        return bbox
            .let(::rectToPolygon)
            .let { transformRing(it, transform, SAMPLING_EPSILON) }
            .let { DoubleRectangles.boundingBox(it) }
            .let { Typed.Rectangle<OutT>(
                Typed.Point<OutT>(it.origin.x, it.origin.y),
                Typed.Point<OutT>(it.dimension.x, it.dimension.y)
            ) }
    }

    fun <InT, OutT> transformMultipolygon(
        multiPolygon: Typed.MultiPolygon<InT>,
        transform: (Typed.Point<InT>) -> Typed.Point<OutT>
    ): Typed.MultiPolygon<OutT> {
        val xyMultipolygon = ArrayList<Typed.Polygon<OutT>>(multiPolygon.size)
        multiPolygon.forEach { xyMultipolygon.add(transformPolygon(it, transform, SAMPLING_EPSILON)) }
        return Typed.MultiPolygon<OutT>(xyMultipolygon)
    }

    private fun <InT, OutT> transformPolygon(
        polygon: Typed.Polygon<InT>,
        transform: (Typed.Point<InT>) -> Typed.Point<OutT>,
        epsilon: Double
    ): Typed.Polygon<OutT> {
        val xyPolygon = ArrayList<Typed.Ring<OutT>>(polygon.size)
        polygon.forEach { ring -> xyPolygon.add(Typed.Ring<OutT>(transformRing(ring, transform, epsilon))) }
        return Typed.Polygon<OutT>(xyPolygon)
    }

    private fun <InT, OutT> transformRing(
        path: List<Typed.Point<InT>>,
        transform: (Typed.Point<InT>) -> Typed.Point<OutT>,
        epsilon: Double
    ): List<Typed.Point<OutT>> {
        return AdaptiveResampling(transform, epsilon).resample(path)
    }

    fun <InT, OutT> transform(
        multiPolygon: Typed.MultiPolygon<InT>,
        transform: (Typed.Point<InT>) -> Typed.Point<OutT>
    ): Typed.MultiPolygon<OutT> {
        val xyMultipolygon = ArrayList<Typed.Polygon<OutT>>(multiPolygon.size)
        multiPolygon.forEach { polygon -> xyMultipolygon.add(transform(polygon, transform, SAMPLING_EPSILON)) }
        return Typed.MultiPolygon<OutT>(xyMultipolygon)
    }

    private fun <InT, OutT> transform(
        polygon: Typed.Polygon<InT>,
        transform: (Typed.Point<InT>) -> Typed.Point<OutT>,
        epsilon: Double
    ): Typed.Polygon<OutT> {
        val xyPolygon = ArrayList<Typed.Ring<OutT>>(polygon.size)
        polygon.forEach { ring -> xyPolygon.add(Typed.Ring<OutT>(transform(ring, transform, epsilon))) }
        return Typed.Polygon<OutT>(xyPolygon)
    }

    private fun <InT, OutT> transform(
        path: List<Typed.Point<InT>>,
        transform: (Typed.Point<InT>) -> Typed.Point<OutT>,
        epsilon: Double
    ): List<Typed.Point<OutT>> {
        val res = ArrayList<Typed.Point<OutT>>(path.size)
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

    fun <ProjT> safePoint(x: Double, y: Double): Typed.Point<ProjT> {
        return if (x.isNaN() || y.isNaN()) {
            error("Value for DoubleVector isNaN x = $x and y = $y")
        } else {
            Typed.Point(x, y)
        }
    }
}