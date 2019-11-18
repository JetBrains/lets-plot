/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.sampling.method

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.spatial.GeoUtils.calculateArea
import jetbrains.datalore.base.values.Pair
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.DataFrame.Variable
import jetbrains.datalore.plot.base.data.TransformVar
import jetbrains.datalore.plot.base.stat.Stats
import jetbrains.datalore.plot.base.util.MutableDouble
import jetbrains.datalore.plot.base.util.MutableInteger
import jetbrains.datalore.plot.builder.sampling.method.VertexSampling.DoubleVectorComponentsList
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.random.Random

internal object SamplingUtil {

    fun groupCount(groupMapper: (Int) -> Int, size: Int): Int {
        return (0 until size).map { groupMapper(it) }.distinct().count()
    }

    fun distinctGroups(groupMapper: (Int) -> Int, size: Int): MutableList<Int> {
        return (0 until size).map { groupMapper(it) }.distinct().toMutableList()
    }

    //static DataFrame randomSample(int sampleSize, DataFrame population, Random rand) {
    //  int popSize = population.rowCount();
    //  boolean pickIndices = sampleSize <= popSize / 2;
    //  int indexCount = pickIndices ? sampleSize : (popSize - sampleSize);
    //
    //  Set<Integer> s = randomIndices(popSize, indexCount, rand);
    //  if (pickIndices) {
    //    return population.selectIndices(s);
    //  }
    //  return population.dropIndices(s);
    //}

    //private static Set<Integer> randomIndices(int indexedCollectionSize, int count, Random rand) {
    //  Set<Integer> indices = new HashSet<>();
    //  while (indices.size() < count) {
    //    indices.add(rand.nextInt(indexedCollectionSize));
    //  }
    //  return indices;
    //}

    fun <T> sampleWithoutReplacement(popSize: Int, sampleSize: Int, rand: Random, onPick: (Set<Int>) -> T,
                                     onDrop: (Set<Int>) -> T): T {
        val pick = sampleSize <= popSize / 2
        val indexCount = if (pick) sampleSize else popSize - sampleSize

        //Set<Integer> s = randomIndices(popSize, indexCount, rand);

        val indexSet = HashSet<Int>()
        while (indexSet.size < indexCount) {
            indexSet.add(rand.nextInt(popSize))
        }

        return if (pick) onPick(indexSet) else onDrop(indexSet)
    }

    fun xVar(data: DataFrame): Variable {
        if (data.has(Stats.X)) {
            return Stats.X
        } else if (data.has(TransformVar.X)) {
            return TransformVar.X
        }
        throw IllegalStateException("Can't apply sampling: couldn't deduce the (X) variable")
    }

    fun yVar(data: DataFrame): Variable {
        if (data.has(Stats.Y)) {
            return Stats.Y
        } else if (data.has(TransformVar.Y)) {
            return TransformVar.Y
        }
        throw IllegalStateException("Can't apply sampling: couldn't deduce the (Y) variable")
    }

    fun splitRings(population: DataFrame): List<List<DoubleVector>> {
        val rings = ArrayList<List<DoubleVector>>()
        var lastPoint: DoubleVector? = null
        var start = -1

        val xValues = population[xVar(population)] as List<Any>
        val yValues = population[yVar(population)] as List<Any>
        val points = DoubleVectorComponentsList(xValues, yValues)
        for (i in points.indices) {
            val point = points[i]
            if (start < 0) {
                start = i
                lastPoint = point
            } else if (lastPoint == point) {
                rings.add(points.subList(start, i + 1))
                start = -1
                lastPoint = null
            }
        }
        if (start >= 0) {
            // not closed
            rings.add(points.subList(start, points.size))
        }
        return rings
    }

    fun calculateRingLimits(rings: List<List<DoubleVector>>, totalPointsLimit: Int): List<Int> {
        val totalArea = rings.map { calculateArea(it) }.sum()

        val areaProceed = MutableDouble(0.0)
        val pointsProceed = MutableInteger(0)

        return rings.indices
            .asSequence()
            .map { Pair(it, calculateArea(rings[it])) }
            .sortedWith(compareBy<Pair<*, Double>> {
                getRingArea(
                    it
                )
            }.reversed())
            .map { p ->
                var limit = min((p.second / (totalArea - areaProceed.get()) * (totalPointsLimit - pointsProceed.get())).roundToInt(),
                    rings[getRingIndex(p)].size
                )

                if (limit >= 4) {
                    areaProceed.getAndAdd(getRingArea(p))
                    pointsProceed.getAndAdd(limit)
                } else {
                    limit = 0
                }

                Pair(getRingIndex(p), limit)
            }
            .sortedWith(compareBy { getRingIndex(it) })
            .map { getRingLimit(it) }
            .toList()
    }

    fun getRingIndex(pair: Pair<Int, *>): Int {
        return pair.first
    }

    private fun getRingArea(pair: Pair<*, Double>): Double {
        return pair.second
    }

    fun getRingLimit(pair: Pair<*, Int>): Int {
        return pair.second
    }
}
