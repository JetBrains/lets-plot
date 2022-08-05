/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.geometry

import jetbrains.datalore.base.typedGeometry.Geometry
import jetbrains.datalore.base.typedGeometry.Geometry.Companion.createMultiLineString
import jetbrains.datalore.base.typedGeometry.Geometry.Companion.createMultiPoint
import jetbrains.datalore.base.typedGeometry.Geometry.Companion.createMultiPolygon
import jetbrains.datalore.base.typedGeometry.GeometryType.*
import jetbrains.datalore.base.typedGeometry.MultiPolygon
import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.datalore.base.typedGeometry.VecResampler
import jetbrains.livemap.core.multitasking.MicroTask
import jetbrains.livemap.core.multitasking.map

private const val SAMPLING_EPSILON = 0.001

object GeometryTransform {
    fun <InT, OutT> resampling(
        geometry: Geometry<InT>,
        transform: (Vec<InT>) -> Vec<OutT>?
    ): MicroTask<Geometry<OutT>> {
        return createTransformer(geometry, resampling(transform))
    }

    fun <InT, OutT> simple(
        geometry: MultiPolygon<InT>,
        transform: (Vec<InT>) -> Vec<OutT>?
    ): MicroTask<MultiPolygon<OutT>> {
        return MultiPolygonTransform(geometry, simple(transform))
    }

    fun <InT, OutT> resampling(
        geometry: MultiPolygon<InT>,
        transform: (Vec<InT>) -> Vec<OutT>?
    ): MicroTask<MultiPolygon<OutT>> {
        return MultiPolygonTransform(geometry, resampling(transform))
    }

    private fun <InT, OutT> simple(
        transform: (Vec<InT>) -> Vec<OutT>?
    ): (Vec<InT>, MutableCollection<Vec<OutT>>) -> Unit {
        return { p, ring -> transform(p)?.let { ring.add(it) } }
    }

    private fun <InT, OutT> resampling(
        transform: (Vec<InT>) -> Vec<OutT>?
    ): (Vec<InT>, MutableCollection<Vec<OutT>>) -> Unit = IterativeResampler(transform)::next

    private fun <InT, OutT> createTransformer(
        geometry: Geometry<InT>,
        transform: (Vec<InT>, MutableCollection<Vec<OutT>>) -> Unit
    ): MicroTask<Geometry<OutT>> {
        return when (geometry.type) {
            MULTI_POLYGON ->
                MultiPolygonTransform(geometry.multiPolygon, transform).map(::createMultiPolygon)
            MULTI_LINESTRING ->
                MultiLineStringTransform(geometry.multiLineString, transform).map(::createMultiLineString)
            MULTI_POINT ->
                MultiPointTransform(geometry.multiPoint, transform).map(::createMultiPoint)
        }
    }

    internal class IterativeResampler<InT, OutT>(
        private val myTransform: (Vec<InT>) -> Vec<OutT>?
    ) {
        private val myAdaptiveResampling = VecResampler(myTransform, SAMPLING_EPSILON)
        private var myPrevPoint: Vec<InT>? = null
        private var myRing: MutableCollection<Vec<OutT>>? = null

        fun next(p: Vec<InT>, ring: MutableCollection<Vec<OutT>>) {
            if (myRing == null || // first call
                ring !== myRing
            ) { // next ring
                myRing = ring
                myPrevPoint = null
            }

            val prev = myPrevPoint
            myPrevPoint = p

            when (prev) {
                null -> myTransform(p)?.let(myRing!!::add)
                else -> myAdaptiveResampling.resample(prev, p).drop(1).forEach(myRing!!::add)
            }
        }
    }
}