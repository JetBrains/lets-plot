package jetbrains.livemap.entities.geometry

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.projectionGeometry.MultiPolygon
import jetbrains.gis.common.twkb.Twkb
import jetbrains.gis.tileprotocol.TileFeature
import jetbrains.gis.tileprotocol.TileFeature.TileGeometry.Companion.createMultiLineString
import jetbrains.gis.tileprotocol.TileFeature.TileGeometry.Companion.createMultiPoint
import jetbrains.gis.tileprotocol.TileFeature.TileGeometry.Companion.createMultiPolygon
import jetbrains.livemap.core.multitasking.MicroTask
import jetbrains.livemap.projections.AdaptiveResampling
import jetbrains.livemap.projections.ProjectionUtil.SAMPLING_EPSILON

object GeometryTransform {
    fun resampling(
        geometry: TileFeature.TileGeometry,
        transform: (DoubleVector) -> DoubleVector
    ): MicroTask<TileFeature.TileGeometry> {
        return createTransformer(geometry, resampling(transform))
    }

    fun simple(geometry: MultiPolygon, transform: (DoubleVector) -> DoubleVector): MicroTask<MultiPolygon> {
        return MultiPolygonTransform(geometry, simple(transform))
    }

    fun resampling(geometry: MultiPolygon, transform: (DoubleVector) -> DoubleVector): MicroTask<MultiPolygon> {
        return MultiPolygonTransform(geometry, resampling(transform))
    }

    private fun simple(transform: (DoubleVector) -> DoubleVector): (DoubleVector, MutableCollection<DoubleVector>) -> Unit {
        return { p, ring -> ring.add(transform(p)) }
    }

    private fun resampling(transform: (DoubleVector) -> DoubleVector): (DoubleVector, MutableCollection<DoubleVector>) -> Unit {
        return { p, ring ->
            IterativeResampler(transform).next(p, ring)
        }
    }

    private fun createTransformer(
        geometry: TileFeature.TileGeometry,
        transform: (DoubleVector, MutableCollection<DoubleVector>) -> Unit
    ): MicroTask<TileFeature.TileGeometry> {
        return when (geometry.type) {
            Twkb.GeometryType.MULTI_POLYGON ->
                MultiPolygonTransform(geometry.multiPolygon!!, transform).map(::createMultiPolygon)
            Twkb.GeometryType.MULTI_LINESTRING ->
                MultiLineStringTransform(geometry.multiLineString!!, transform).map(::createMultiLineString)
            Twkb.GeometryType.MULTI_POINT ->
                MultiPointTransform(geometry.multiPoint!!, transform).map(::createMultiPoint)
            else ->
                throw IllegalArgumentException("Unsupported geometry type: ${geometry.type}")
        }
    }

    internal class IterativeResampler(private val myTransform: (DoubleVector) -> DoubleVector) {
        private val myAdaptiveResampling = AdaptiveResampling(myTransform, SAMPLING_EPSILON)
        private var myPrevPoint: DoubleVector? = null
        private lateinit var myRing: MutableCollection<DoubleVector>

        fun next(p: DoubleVector, ring: MutableCollection<DoubleVector>) {
            if (ring !== myRing) {
                myRing = ring
                myPrevPoint = null
            }

            resample(p).forEach { newPoint -> myRing.add(myTransform(newPoint)) }
        }

        private fun resample(p: DoubleVector): List<DoubleVector> {
            val prev = myPrevPoint
            myPrevPoint = p

            return when {
                prev != null -> myAdaptiveResampling.resample(prev, p).run { subList(1, size) }
                else -> listOf(p)
            }
        }
    }
}