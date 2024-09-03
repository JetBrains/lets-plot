/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.sampling.method

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.typedGeometry.algorithms.isClosed
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil
import org.jetbrains.letsPlot.core.commons.geometry.PolylineSimplifier
import org.jetbrains.letsPlot.core.commons.mutables.MutableInteger
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.builder.sampling.PointSampling
import org.jetbrains.letsPlot.core.plot.builder.sampling.method.SamplingUtil.calculateRingLimits
import org.jetbrains.letsPlot.core.plot.builder.sampling.method.SamplingUtil.getRingIndex
import org.jetbrains.letsPlot.core.plot.builder.sampling.method.SamplingUtil.getRingLimit
import org.jetbrains.letsPlot.core.plot.builder.sampling.method.SamplingUtil.splitRings

internal abstract class PolygonSampling(sampleSize: Int) : SamplingBase(sampleSize),
    PointSampling {

    private fun simplify(points: List<DoubleVector>, limit: Int): List<Int> {
        return if (limit == 0) {
            emptyList()
        } else {
            simplifyInternal(points, limit)
        }
    }

    internal abstract fun simplifyInternal(points: List<DoubleVector>, limit: Int): List<Int>

    override fun apply(population: DataFrame): DataFrame {
        require(isApplicable(population))

        val rings = splitRings(population)
        val limits = if (rings.size == 1 && !rings[0].isClosed())
            listOf(sampleSize)
        else
            calculateRingLimits(rings, sampleSize)

        val indices = ArrayList<Int>()
        val ringBase = MutableInteger(0)

        (0 until limits.size)
            .map { Pair(it, limits[it]) }
            .forEach { p ->
                simplify(rings[getRingIndex(p)], getRingLimit(p))
                    .forEach { index -> indices.add(ringBase.get() + index) }
                ringBase.getAndAdd(rings[getRingIndex(p)].size)
            }

        return population.selectIndices(indices)
    }

    internal class PolygonVwSampling(sampleSize: Int) : PolygonSampling(sampleSize) {

        override val expressionText: String
            get() = "sampling_" + ALIAS + "(" +
                    "n=" + sampleSize + ")"

        override fun simplifyInternal(points: List<DoubleVector>, limit: Int): List<Int> {
            return PolylineSimplifier.visvalingamWhyatt(points).setCountLimit(limit).indices.single()
        }

        companion object {
            const val ALIAS = "vertex_vw"
        }
    }

    internal class PolygonDpSampling(sampleSize: Int) : PolygonSampling(sampleSize) {

        override val expressionText: String
            get() = "sampling_" + ALIAS + "(" +
                    "n=" + sampleSize + ")"

        override fun simplifyInternal(points: List<DoubleVector>, limit: Int): List<Int> {
            return PolylineSimplifier.douglasPeucker(points).setCountLimit(limit).indices.single()
        }

        companion object {
            const val ALIAS = "vertex_dp"
        }
    }

    internal class DoubleVectorComponentsList(private val myXValues: List<Any>, private val myYValues: List<Any>) :
        AbstractList<DoubleVector>() {
        override val size: Int
            get() = myXValues.size

        override fun get(index: Int): DoubleVector {
            return createPoint(
                myXValues[index],
                myYValues[index]
            )
        }
    }

    companion object {
        private fun createPoint(x: Any, y: Any): DoubleVector {
            require(!(x is String || y is String)) { "String coords are not supported yet" }
            require(SeriesUtil.allFinite(x as Double, y as Double)) { "Invalid coord" }
            return DoubleVector(x, y)
        }
    }
}
