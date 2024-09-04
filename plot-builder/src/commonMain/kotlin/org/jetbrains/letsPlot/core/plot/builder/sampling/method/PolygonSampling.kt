/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.sampling.method

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.commons.geometry.PolylineSimplifier
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.builder.sampling.PointSampling
import org.jetbrains.letsPlot.core.plot.builder.sampling.method.SamplingUtil.readPolygon

internal abstract class PolygonSampling(
    sampleSize: Int
) : SamplingBase(sampleSize), PointSampling {

    internal abstract fun simplifyInternal(points: List<List<DoubleVector>>, limit: Int): List<List<Int>>

    override fun apply(population: DataFrame): DataFrame {
        require(isApplicable(population))

        // TODO: check how groups work

        val sourceRings = readPolygon(population)

        val rings = sourceRings.map { ring -> ring.map { (_, p) -> p } } // leave only coordinates
        val simplificationIndex = simplifyInternal(rings, sampleSize)

        // restore data frame indices from the simplified path indices
        val dataIndices = sourceRings.zip(simplificationIndex)
            .flatMap { (ring, subIndices) -> ring.slice(subIndices) }
            .map(IndexedValue<DoubleVector>::index)

        return population.selectIndices(dataIndices)
    }

    internal class PolygonVwSampling(sampleSize: Int) : PolygonSampling(sampleSize) {

        override val expressionText: String
            get() = "sampling_" + ALIAS + "(" +
                    "n=" + sampleSize + ")"

        override fun simplifyInternal(points: List<List<DoubleVector>>, limit: Int): List<List<Int>> {
            return PolylineSimplifier.visvalingamWhyattMultipath(points).setCountLimit(limit).indices
        }

        companion object {
            const val ALIAS = "polygon_vw"
        }
    }

    internal class PolygonDpSampling(sampleSize: Int) : PolygonSampling(sampleSize) {

        override val expressionText: String
            get() = "sampling_" + ALIAS + "(" +
                    "n=" + sampleSize + ")"

        override fun simplifyInternal(points: List<List<DoubleVector>>, limit: Int): List<List<Int>> {
            return PolylineSimplifier.douglasPeuckerMultipath(points).setCountLimit(limit).indices
        }

        companion object {
            const val ALIAS = "polygon_dp"
        }
    }
}
