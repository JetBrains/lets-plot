/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.projections

import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.datalore.base.typedGeometry.div
import jetbrains.datalore.base.typedGeometry.plus
import kotlin.math.sqrt

class AdaptiveResampling<InT, OutT>(
    private val transform: (Vec<InT>) -> Vec<OutT>,
    epsilon: Double
) {
    private val epsilonSqr: Double = epsilon * epsilon

    private fun <T> MutableList<T>.pop(): T {
        return get(lastIndex).also { removeAt(lastIndex) }
    }

    fun resample(points: List<Vec<InT>>): List<Vec<OutT>> {
        val result = ArrayList<Vec<OutT>>(points.size)

        for (i in 1 until points.size) {
            val sample = resample(points[i - 1], points[i])

            if (!result.isEmpty()) {
                result.pop()
            }

            sample.forEach { p -> result.add(transform(p)) }
        }

        return result
    }

    fun resample(p1: Vec<InT>, p2: Vec<InT>): List<Vec<InT>> {
        val result = ArrayList<Vec<InT>>()
        val candidates = ArrayList<Vec<InT>>()
        result.add(p1)
        candidates.add(p2)

        while (!candidates.isEmpty()) {
            val samplePoint = getSamplePoint(result.last(), candidates.last())

            if (samplePoint == null) {
                result.add(candidates.pop())
            } else {
                candidates.add(samplePoint)
            }
        }
        return result
    }

    private fun getSamplePoint(p1: Vec<InT>, p2: Vec<InT>): Vec<InT>? {
        val pc = (p1 + p2) / 2.0;
        val q1 = transform(p1)
        val q2 = transform(p2)
        val qc = transform(pc)

        val distance = if (q1 == q2) {
            length(q1, qc)
        } else {
            distance(qc, q1, q2)
        }

        return if (distance < epsilonSqr) null else pc
    }


    private fun length(p1: Vec<*>, p2: Vec<*>): Double {
        val x = p2.x - p1.x
        val y = p2.y - p1.y
        return x * x + y * y
    }

    private fun distance(p: Vec<*>, l1: Vec<*>, l2: Vec<*>): Double {
        val ortX = l2.x - l1.x
        val ortY = -(l2.y - l1.y)

        val dot = (p.x - l1.x) * ortY + (p.y - l1.y) * ortX
        val len = ortY * ortY + ortX * ortX

        return dot * dot / len
    }
}