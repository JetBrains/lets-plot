/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.sampling.method

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.commons.geometry.PolylineSimplifier
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.builder.data.GroupUtil.indicesByGroup
import org.jetbrains.letsPlot.core.plot.builder.sampling.method.SamplingUtil.readPath

internal abstract class PathSampling(
    sampleSize: Int
) : GroupSamplingBase(sampleSize) {

    internal abstract fun simplifyInternal(points: List<List<DoubleVector>>, limit: Int): List<List<Int>>

    override fun isApplicable(population: DataFrame, groupMapper: (Int) -> Int, groupCount: Int): Boolean {
        return population.rowCount() >= sampleSize
    }

    override fun apply(population: DataFrame, groupMapper: (Int) -> Int): DataFrame {
        require(isApplicable(population))

        val points = readPath(population, multipath = false).single()

        val paths = indicesByGroup(population.rowCount(), groupMapper)
            .entries
            .map { (_, indices) -> points.slice(indices) }

        val coords = paths.map { path -> path.map(IndexedValue<DoubleVector>::value) }
        val simplificationIndex = simplifyInternal(coords, sampleSize)

        // restore data frame indices from the simplified path indices
        val dataIndices = paths.zip(simplificationIndex)
            .flatMap { (subPath, subIndices) -> subPath.slice(subIndices) }
            .map(IndexedValue<DoubleVector>::index)

        return population.selectIndices(dataIndices)
    }

    internal class PathVwSampling(sampleSize: Int) : PathSampling(sampleSize) {

        override val expressionText: String
            get() = "sampling_" + ALIAS + "(" +
                    "n=" + sampleSize + ")"

        override fun simplifyInternal(points: List<List<DoubleVector>>, limit: Int): List<List<Int>> {
            return PolylineSimplifier.visvalingamWhyattMultipath(points).setCountLimit(limit).indices
        }

        companion object {
            const val ALIAS = "path_vw"
        }
    }

    internal class PathDpSampling(sampleSize: Int) : PathSampling(sampleSize) {

        override val expressionText: String
            get() = "sampling_" + ALIAS + "(" +
                    "n=" + sampleSize + ")"

        override fun simplifyInternal(points: List<List<DoubleVector>>, limit: Int): List<List<Int>> {
            return PolylineSimplifier.douglasPeuckerMultipath(points).setCountLimit(limit).indices
        }

        companion object {
            const val ALIAS = "path_dp"
        }
    }
}