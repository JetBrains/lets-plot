/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.sampling.method

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.typedGeometry.algorithms.splitRings
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.DataFrame.Variable
import org.jetbrains.letsPlot.core.plot.base.data.TransformVar
import org.jetbrains.letsPlot.core.plot.base.stat.Stats

internal object SamplingUtil {

    fun groupCount(groupMapper: (Int) -> Int, size: Int): Int {
        return (0 until size).map { groupMapper(it) }.distinct().count()
    }

    fun distinctGroups(groupMapper: (Int) -> Int, size: Int): MutableList<Int> {
        return (0 until size).map { groupMapper(it) }.distinct().toMutableList()
    }

    fun xVar(variables: Set<Variable>): Variable? {
        return when {
            Stats.X in variables -> Stats.X
            TransformVar.X in variables -> TransformVar.X
            else -> null
        }
    }

    fun xVar(data: DataFrame): Variable {
        return xVar(data.variables())
            ?: throw IllegalStateException("Can't apply sampling: couldn't deduce the (X) variable.")
    }

    fun yVar(data: DataFrame): Variable {
        if (data.has(Stats.Y)) {
            return Stats.Y
        } else if (data.has(TransformVar.Y)) {
            return TransformVar.Y
        }
        throw IllegalStateException("Can't apply sampling: couldn't deduce the (Y) variable.")
    }

    internal fun readPoints(population: DataFrame): List<IndexedValue<DoubleVector?>> {
        val xVar = xVar(population)
        val yVar = yVar(population)
        return population[xVar].asSequence()
            .zip(population[yVar].asSequence())
            .mapIndexed { i, (x, y) ->
                when {
                    x as? Double == null -> IndexedValue(i, null)
                    y as? Double == null -> IndexedValue(i, null)
                    else -> IndexedValue(i, DoubleVector(x, y))
                }
            }.toList()
    }

    fun readPolygon(population: DataFrame): List<List<IndexedValue<DoubleVector>>> {
        @Suppress("UNCHECKED_CAST")
        val points = readPoints(population)
            .filter { it.value != null } as List<IndexedValue<DoubleVector>> // to mark rings usually use equal values, not nulls

        return splitRings(points) { p1, p2 -> p1.value == p2.value }
    }
}
