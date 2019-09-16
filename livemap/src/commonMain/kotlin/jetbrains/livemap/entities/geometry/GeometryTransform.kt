package jetbrains.livemap.entities.geometry

import jetbrains.datalore.base.projectionGeometry.Typed
import jetbrains.gis.common.twkb.Twkb
import jetbrains.livemap.core.multitasking.MicroTask
import jetbrains.livemap.projections.AdaptiveResampling
import jetbrains.livemap.projections.ProjectionUtil.SAMPLING_EPSILON
import jetbrains.livemap.tiles.TileFeature
import jetbrains.livemap.tiles.TileFeature.TileGeometry.Companion.createMultiLineString
import jetbrains.livemap.tiles.TileFeature.TileGeometry.Companion.createMultiPoint
import jetbrains.livemap.tiles.TileFeature.TileGeometry.Companion.createMultiPolygon

object GeometryTransform {
    fun <InT, OutT> resampling(
        geometry: TileFeature.TileGeometry,
        transform: (Typed.Point<InT>) -> Typed.Point<OutT>
    ): MicroTask<TileFeature.TileGeometry> {
        return createTransformer(geometry, resampling(transform))
    }

    fun <InT, OutT> simple(
        geometry: Typed.MultiPolygon<InT>,
        transform: (Typed.Point<InT>) -> Typed.Point<OutT>
    ): MicroTask<Typed.MultiPolygon<OutT>> {
        return MultiPolygonTransform(geometry, simple(transform))
    }

    fun <InT, OutT> resampling(
        geometry: Typed.MultiPolygon<InT>,
        transform: (Typed.Point<InT>) -> Typed.Point<OutT>
    ): MicroTask<Typed.MultiPolygon<OutT>> {
        return MultiPolygonTransform(geometry, resampling(transform))
    }

    private fun <InT, OutT> simple(
        transform: (Typed.Point<InT>) -> Typed.Point<OutT>
    ): (Typed.Point<InT>, MutableCollection<Typed.Point<OutT>>) -> Unit {
        return { p, ring -> ring.add(transform(p)) }
    }

    private fun <InT, OutT> resampling(
        transform: (Typed.Point<InT>) -> Typed.Point<OutT>
    ): (Typed.Point<InT>, MutableCollection<Typed.Point<OutT>>) -> Unit {
        return { p, ring -> IterativeResampler(transform).next(p, ring) }
    }

    private fun <InT, OutT> createTransformer(
        geometry: TileFeature.TileGeometry,
        transform: (Typed.Point<InT>, MutableCollection<Typed.Point<OutT>>) -> Unit
    ): MicroTask<TileFeature.TileGeometry> {
        return when (geometry.type) {
            Twkb.GeometryType.MULTI_POLYGON ->
                MultiPolygonTransform<InT, OutT>(geometry.multiPolygon!!.reinterpret(), transform).map(::createMultiPolygon)
            Twkb.GeometryType.MULTI_LINESTRING ->
                MultiLineStringTransform<InT, OutT>(geometry.multiLineString!!.reinterpret(), transform).map(::createMultiLineString)
            Twkb.GeometryType.MULTI_POINT ->
                MultiPointTransform<InT, OutT>(geometry.multiPoint!!.reinterpret(), transform).map(::createMultiPoint)
            else ->
                throw IllegalArgumentException("Unsupported geometry type: ${geometry.type}")
        }
    }

    internal class IterativeResampler<InT, OutT>(
        private val myTransform: (Typed.Point<InT>) -> Typed.Point<OutT>
    ) {
        private val myAdaptiveResampling = AdaptiveResampling(myTransform, SAMPLING_EPSILON)
        private var myPrevPoint: Typed.Point<InT>? = null
        private var myRing: MutableCollection<Typed.Point<OutT>>? = null

        fun next(p: Typed.Point<InT>, ring: MutableCollection<Typed.Point<OutT>>) {
            if (myRing == null || // first call
                ring != myRing) { // next ring
                myRing = ring
                myPrevPoint = null
            }

            resample(p).forEach { newPoint -> myRing!!.add(myTransform(newPoint)) }
        }

        private fun resample(p: Typed.Point<InT>): List<Typed.Point<InT>> {
            val prev = myPrevPoint
            myPrevPoint = p

            return when {
                prev != null -> myAdaptiveResampling.resample(prev, p).run { subList(1, size) }
                else -> listOf(p)
            }
        }
    }
}