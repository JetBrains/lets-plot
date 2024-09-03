/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.sampling.method

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.typedGeometry.algorithms.isClosed
import org.jetbrains.letsPlot.commons.intern.typedGeometry.algorithms.readPath
import org.jetbrains.letsPlot.core.commons.mutables.MutableInteger
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.builder.sampling.PointSampling
import org.jetbrains.letsPlot.core.plot.builder.sampling.method.SamplingUtil.calculateRingLimits
import org.jetbrains.letsPlot.core.plot.builder.sampling.method.SamplingUtil.getRingIndex
import org.jetbrains.letsPlot.core.plot.builder.sampling.method.SamplingUtil.getRingLimit

internal abstract class PathSampling(sampleSize: Int) : SamplingBase(sampleSize),
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

        val xVar = SamplingUtil.xVar(population)
        val yVar = SamplingUtil.yVar(population)
        val points = population[xVar].asSequence()
            .zip(population[yVar].asSequence())
            .map { (x, y) ->
                @Suppress("NAME_SHADOWING")
                val x = x as? Double ?: return@map null
                @Suppress("NAME_SHADOWING")
                val y = y as? Double ?: return@map null

                DoubleVector(x, y)
            }.toList()

        val rings = readPath(points)
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
}