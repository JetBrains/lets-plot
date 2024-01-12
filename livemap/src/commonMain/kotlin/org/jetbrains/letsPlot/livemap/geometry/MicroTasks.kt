/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.geometry

import org.jetbrains.letsPlot.commons.intern.typedGeometry.Geometry
import org.jetbrains.letsPlot.commons.intern.typedGeometry.GeometryType.*
import org.jetbrains.letsPlot.commons.intern.typedGeometry.MultiPolygon
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Vec
import org.jetbrains.letsPlot.commons.intern.typedGeometry.VecResampler.Companion.resample
import org.jetbrains.letsPlot.livemap.core.multitasking.MicroTask
import org.jetbrains.letsPlot.livemap.core.multitasking.map


object MicroTasks {
    const val RESAMPLING_PRECISION = 0.004

    fun <InT, OutT> resample(
        geometry: Geometry<InT>,
        transform: (Vec<InT>) -> Vec<OutT>?
    ): MicroTask<Geometry<OutT>> {
        return createTransformer(geometry, resample(transform))
    }

    fun <InT, OutT> transform(
        geometry: Geometry<InT>,
        transform: (Vec<InT>) -> Vec<OutT>?
    ): MicroTask<Geometry<OutT>> {
        return createTransformer(geometry, transform(transform))
    }

    fun <InT, OutT> transform(
        geometry: MultiPolygon<InT>,
        transform: (Vec<InT>) -> Vec<OutT>?
    ): MicroTask<MultiPolygon<OutT>> {
        return MultiPolygonTransform(geometry, transform(transform))
    }

    fun <InT, OutT> resample(
        geometry: MultiPolygon<InT>,
        transform: (Vec<InT>) -> Vec<OutT>?
    ): MicroTask<MultiPolygon<OutT>> {
        return MultiPolygonTransform(geometry, resample(transform))
    }

    private fun <InT, OutT> transform(
        transform: (Vec<InT>) -> Vec<OutT>?
    ): (Vec<InT>, MutableCollection<Vec<OutT>>) -> Unit {
        return { p, ring -> transform(p)?.let(ring::add) }
    }

    private fun <InT, OutT> resample(
        transform: (Vec<InT>) -> Vec<OutT>?
    ): (Vec<InT>, MutableCollection<Vec<OutT>>) -> Unit = IterativeResampler(transform)::next

    private fun <InT, OutT> createTransformer(
        geometry: Geometry<InT>,
        transform: (Vec<InT>, MutableCollection<Vec<OutT>>) -> Unit
    ): MicroTask<Geometry<OutT>> {
        return when (geometry.type) {
            MULTI_POLYGON -> MultiPolygonTransform(geometry.multiPolygon, transform).map(Geometry.Companion::of)
            MULTI_LINESTRING -> MultiLineStringTransform(geometry.multiLineString, transform).map(Geometry.Companion::of)
            MULTI_POINT -> MultiPointTransform(geometry.multiPoint, transform).map(Geometry.Companion::of)
        }
    }

    internal class IterativeResampler<InT, OutT>(
        private val myTransform: (Vec<InT>) -> Vec<OutT>?
    ) {
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
                else -> myRing!!.addAll(resample(prev, p, RESAMPLING_PRECISION, myTransform).asSequence().drop(1))
            }
        }
    }
}