/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.core

import org.jetbrains.letsPlot.commons.intern.typedGeometry.Vec
import org.jetbrains.letsPlot.commons.intern.typedGeometry.explicitVec
import kotlin.math.pow

object Transforms {
    fun <InT, OutT> zoom(level: () -> Int): Transform<Vec<InT>, Vec<OutT>> {
        return tuple(
            scale(zoomFactor(level)),
            scale(zoomFactor(level)),
        )
    }

    fun zoomFactor(level: () -> Number): () -> Double {
        return { zoomFactor(level().toDouble()) }
    }

    fun zoomFactor(level: Number): Double {
        return 2.0.pow(level.toDouble())
    }

    internal fun <InT, OutT> tuple(
        xTransform: Transform<Double, Double>,
        yTransform: Transform<Double, Double>
    ): Transform<Vec<InT>, Vec<OutT>> {
        return object : Transform<Vec<InT>, Vec<OutT>> {
            override fun apply(v: Vec<InT>): Vec<OutT> = explicitVec(xTransform.apply(v.x), yTransform.apply(v.y))
            override fun invert(v: Vec<OutT>): Vec<InT> = explicitVec(xTransform.invert(v.x), yTransform.invert(v.y))
        }
    }

    internal fun scale(scale: () -> Double): Transform<Double, Double> {
        return object : Transform<Double, Double> {
            override fun apply(v: Double): Double = v * scale()
            override fun invert(v: Double): Double = v / scale()
        }
    }
}