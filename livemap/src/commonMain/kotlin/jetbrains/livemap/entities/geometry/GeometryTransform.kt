package jetbrains.livemap.entities.geometry

import jetbrains.datalore.base.projectionGeometry.Typed
import jetbrains.datalore.base.projectionGeometry.Typed.GeometryType.*
import jetbrains.datalore.base.projectionGeometry.Typed.TileGeometry.Companion.createMultiLineString
import jetbrains.datalore.base.projectionGeometry.Typed.TileGeometry.Companion.createMultiPoint
import jetbrains.datalore.base.projectionGeometry.Typed.TileGeometry.Companion.createMultiPolygon
import jetbrains.datalore.base.projectionGeometry.reinterpret
import jetbrains.livemap.core.multitasking.MicroTask
import jetbrains.livemap.projections.AdaptiveResampling
import jetbrains.livemap.projections.ProjectionUtil.SAMPLING_EPSILON

object GeometryTransform {
    fun <InT, OutT> resampling(
        geometry: Typed.TileGeometry<InT>,
        transform: (Typed.Vec<InT>) -> Typed.Vec<OutT>
    ): MicroTask<Typed.TileGeometry<OutT>> {
        return createTransformer(geometry, resampling(transform))
    }

    fun <InT, OutT> simple(
        geometry: Typed.MultiPolygon<InT>,
        transform: (Typed.Vec<InT>) -> Typed.Vec<OutT>
    ): MicroTask<Typed.MultiPolygon<OutT>> {
        return MultiPolygonTransform(geometry, simple(transform))
    }

    fun <InT, OutT> resampling(
        geometry: Typed.MultiPolygon<InT>,
        transform: (Typed.Vec<InT>) -> Typed.Vec<OutT>
    ): MicroTask<Typed.MultiPolygon<OutT>> {
        return MultiPolygonTransform(geometry, resampling(transform))
    }

    private fun <InT, OutT> simple(
        transform: (Typed.Vec<InT>) -> Typed.Vec<OutT>
    ): (Typed.Vec<InT>, MutableCollection<Typed.Vec<OutT>>) -> Unit {
        return { p, ring -> ring.add(transform(p)) }
    }

    private fun <InT, OutT> resampling(
        transform: (Typed.Vec<InT>) -> Typed.Vec<OutT>
    ): (Typed.Vec<InT>, MutableCollection<Typed.Vec<OutT>>) -> Unit {
        return { p, ring -> IterativeResampler(transform).next(p, ring) }
    }

    private fun <InT, OutT> createTransformer(
        geometry: Typed.TileGeometry<InT>,
        transform: (Typed.Vec<InT>, MutableCollection<Typed.Vec<OutT>>) -> Unit
    ): MicroTask<Typed.TileGeometry<OutT>> {
        return when (geometry.type) {
            MULTI_POLYGON ->
                MultiPolygonTransform(geometry.multiPolygon!!.reinterpret(), transform).map(::createMultiPolygon)
            MULTI_LINESTRING ->
                MultiLineStringTransform(geometry.multiLineString!!.reinterpret(), transform).map(::createMultiLineString)
            MULTI_POINT ->
                MultiPointTransform(geometry.multiPoint!!.reinterpret(), transform).map(::createMultiPoint)
            else ->
                throw IllegalArgumentException("Unsupported geometry type: ${geometry.type}")
        }
    }

    internal class IterativeResampler<InT, OutT>(
        private val myTransform: (Typed.Vec<InT>) -> Typed.Vec<OutT>
    ) {
        private val myAdaptiveResampling = AdaptiveResampling(myTransform, SAMPLING_EPSILON)
        private var myPrevPoint: Typed.Vec<InT>? = null
        private var myRing: MutableCollection<Typed.Vec<OutT>>? = null

        fun next(p: Typed.Vec<InT>, ring: MutableCollection<Typed.Vec<OutT>>) {
            if (myRing == null || // first call
                ring != myRing) { // next ring
                myRing = ring
                myPrevPoint = null
            }

            resample(p).forEach { newPoint -> myRing!!.add(myTransform(newPoint)) }
        }

        private fun resample(p: Typed.Vec<InT>): List<Typed.Vec<InT>> {
            val prev = myPrevPoint
            myPrevPoint = p

            return when {
                prev != null -> myAdaptiveResampling.resample(prev, p).run { subList(1, size) }
                else -> listOf(p)
            }
        }
    }
}