/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.core.projections

import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.datalore.base.typedGeometry.explicitVec
import kotlin.math.PI
import kotlin.math.pow

object Projections {
    fun mercator(): GeoProjection = MercatorProjection()
    fun geographic(): GeoProjection = GeographicProjection()
    fun azimuthalEqualArea(): GeoProjection = AzimuthalEqualAreaProjection()
    fun conicEqualArea(): GeoProjection = ConicEqualAreaProjection(0.0, PI / 3)

    fun <InT, OutT> zoom(factor: () -> Int): Projection<Vec<InT>, Vec<OutT>> {
        return tuple(
            scale { 2.0.pow(factor()) },
            scale { 2.0.pow(factor()) }
        )
    }

    internal fun <InT, OutT> tuple(
        xProjection: Projection<Double, Double>,
        yProjection: Projection<Double, Double>
    ): Projection<Vec<InT>, Vec<OutT>> {
        return object : Projection<Vec<InT>, Vec<OutT>> {
            override fun project(v: Vec<InT>): Vec<OutT> = explicitVec(xProjection.project(v.x), yProjection.project(v.y))
            override fun invert(v: Vec<OutT>): Vec<InT> = explicitVec(xProjection.invert(v.x), yProjection.invert(v.y))
        }
    }

    internal fun scale(scale: () -> Double): Projection<Double, Double> {
        return object : Projection<Double, Double> {
            override fun project(v: Double): Double = v * scale()
            override fun invert(v: Double): Double = v / scale()
        }
    }
}
