/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.sampling.method

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.typedGeometry.algorithms.splitRings
import org.jetbrains.letsPlot.core.commons.geometry.PolylineSimplifier
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.builder.data.GroupUtil.indicesByGroup
import org.jetbrains.letsPlot.core.plot.builder.sampling.method.SamplingUtil.distinctGroups
import org.jetbrains.letsPlot.core.plot.builder.sampling.method.SamplingUtil.readPoints

internal abstract class VertexSampling(
    sampleSize: Int,
    protected val polygon: Boolean
) : GroupSamplingBase(sampleSize) {

    override fun isApplicable(population: DataFrame, groupMapper: (Int) -> Int, groupCount: Int): Boolean {
        return population.rowCount() > sampleSize
    }

    override fun apply(population: DataFrame, groupMapper: (Int) -> Int): DataFrame {
        require(isApplicable(population))

        return when (polygon) {
            true -> resamplePolygon(population, groupMapper)
            false -> resamplePath(population, groupMapper)
        }
    }

    internal fun resamplePolygon(population: DataFrame, groupMapper: (Int) -> Int): DataFrame {
        val points = readPoints(population)

        val groups = distinctGroups(groupMapper, population.rowCount())

        val groupedPoints = indicesByGroup(population.rowCount(), groupMapper)
            .mapValues { (_, groupIndices) -> points.slice(groupIndices) }
            .mapValues { (_, groupPoints) ->
                @Suppress("UNCHECKED_CAST")
                groupPoints.filter { (_, p) -> p != null } as List<IndexedValue<DoubleVector>>
            }

        val groupedRings = groupedPoints
            .mapValues { (_, groupPoints) -> splitRings(groupPoints, eq = { p1, p2 -> p1.value == p2.value}) }

        val flattenedRingsIndex = mutableListOf<IndexedValue<List<Int>>>()
        var base = 0
        for ((group, groupRings) in groupedRings) {
            flattenedRingsIndex.add(IndexedValue(group, groupRings.indices.map { it + base }))
            base += groupRings.size
        }

        // Process all rings at once to check weights across all groups for better simplification
        val flattenSimplificationIndex = groupedRings.values.flatten()
            .map { ring -> ring.map { (_, p) -> p } } // leave only coordinates
            .let { rings -> simplifyInternal(rings, sampleSize) }

        val groupedSimplificationIndex = flattenedRingsIndex.associateBy(
            keySelector = { (group, _) -> group },
            valueTransform = { (_, groupRingsIndices) -> flattenSimplificationIndex.slice(groupRingsIndices) }
        )

        // Peek important points from the rings
        val simplifiedRings = groups.map { group ->
            val ringsSimplification = groupedSimplificationIndex[group]!!
            val rings = groupedRings[group]!!

            rings.zip(ringsSimplification)
                .map { (ring, indices) -> ring.slice(indices) }
        }

        // restore data frame indices from the simplified rings indices
        val dataIndices = simplifiedRings.flatten().flatten().map { it.index }

        return population.selectIndices(dataIndices)
    }

    private fun resamplePath(population: DataFrame, groupMapper: (Int) -> Int): DataFrame {
        val points = readPoints(population)

        val groupedPaths = indicesByGroup(population.rowCount(), groupMapper).values
            .map { indices -> points.slice(indices) }
            .map { groupPoints ->
                @Suppress("UNCHECKED_CAST")
                groupPoints.filter { (_, p) -> p != null } as List<IndexedValue<DoubleVector>>
            }

        val simplificationIndex = groupedPaths
            .map { path -> path.map { (_, p) -> p } } // leave only coordinates
            .let { paths -> simplifyInternal(paths, sampleSize) }

        // restore data frame indices from the simplified path indices
        val dataIndices = groupedPaths.zip(simplificationIndex)
            .flatMap { (path, indices) -> path.slice(indices) }
            .map { it.index }

        return population.selectIndices(dataIndices)
    }

    internal abstract fun simplifyInternal(points: List<List<DoubleVector>>, limit: Int): List<List<Int>>

    internal class VertexVwSampling(sampleSize: Int, polygon: Boolean) : VertexSampling(sampleSize, polygon) {
        override val expressionText: String
            get() = "sampling_$ALIAS(n=$sampleSize, polygon=$polygon)"

        override fun simplifyInternal(points: List<List<DoubleVector>>, limit: Int): List<List<Int>> {
            return PolylineSimplifier.visvalingamWhyattMultipath(points).setCountLimit(limit).indices
        }

        companion object {
            const val ALIAS = "vertex_vw"
        }
    }

    internal class VertexDpSampling(sampleSize: Int, polygon: Boolean) : VertexSampling(sampleSize, polygon) {
        override val expressionText: String
            get() = "sampling_$ALIAS(n=$sampleSize, polygon=$polygon)"

        override fun simplifyInternal(points: List<List<DoubleVector>>, limit: Int): List<List<Int>> {
            return PolylineSimplifier.douglasPeuckerMultipath(points).setCountLimit(limit).indices
        }

        companion object {
            const val ALIAS = "vertex_dp"
        }
    }
}