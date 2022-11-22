/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.StatContext
import jetbrains.datalore.plot.base.data.TransformVar
import jetbrains.datalore.plot.common.data.SeriesUtil
import jetbrains.datalore.plot.common.util.MutableDouble

/**
 * Counts the number of cases at each (x, y) position
 * (or if the weight aesthetic is supplied, the sum of the weights)
 */
internal class Count2dStat : BaseStat(DEF_MAPPING) {

    override fun consumes(): List<Aes<*>> {
        return listOf(Aes.X, Aes.Y, Aes.WEIGHT)
    }

    override fun apply(data: DataFrame, statCtx: StatContext, messageConsumer: (s: String) -> Unit): DataFrame {
        val rowCount = data.rowCount()

        val xs = if (data.has(TransformVar.X)) {
            data.getNumeric(TransformVar.X)
        } else {
            List(rowCount) { 0.0 }
        }
        val ys = if (data.has(TransformVar.Y)) {
            data.getNumeric(TransformVar.Y)
        } else {
            List(rowCount) { 0.0 }
        }
        val weight = BinStatUtil.weightVector(rowCount, data)

        val statX = ArrayList<Double>()
        val statY = ArrayList<Double>()
        val statCount = ArrayList<Double>()

        val countByXY = countByXY(xs, ys, weight)
        for (key in countByXY.keys) {
            statX.add(key.x)
            statY.add(key.y)
            statCount.add(countByXY[key]!!.get())
        }

        return DataFrame.Builder()
            .putNumeric(Stats.X, statX)
            .putNumeric(Stats.Y, statY)
            .putNumeric(Stats.COUNT, statCount)
            .build()
    }

    override fun normalize(dataAfterStat: DataFrame): DataFrame {
        val xs = dataAfterStat.getNumeric(Stats.X).map { it!! }
        val ys = dataAfterStat.getNumeric(Stats.Y).map { it!! }
        val counts = dataAfterStat.getNumeric(Stats.COUNT).map { it!! }

        val countByXY = countByXY(xs, ys, counts)
        val sumStatCount = ArrayList<Double>()
        val prop = ArrayList<Double>()
        for (i in xs.indices) {
            val x = xs[i]
            val y = ys[i]
            val sum = countByXY[DoubleVector(x, y)]!!.get()
            sumStatCount.add(sum)
            prop.add(counts[i] / sum)
        }
        return dataAfterStat.builder()
            .putNumeric(Stats.SUM, sumStatCount)
            .putNumeric(Stats.PROP, prop)
            .build()
    }

    companion object {
        private val DEF_MAPPING: Map<Aes<*>, DataFrame.Variable> = mapOf(
            Aes.X to Stats.X,
            Aes.Y to Stats.Y,
            Aes.SLICE to Stats.COUNT
        )

        private fun countByXY(
            valuesX: List<Double?>,
            valuesY: List<Double?>,
            weight: List<Double?>
        ): Map<DoubleVector, MutableDouble> {
            val result = LinkedHashMap<DoubleVector, MutableDouble>()
            for (i in valuesX.indices) {
                val x = valuesX[i]
                val y = valuesY[i]
                if (SeriesUtil.allFinite(x, y)) {
                    val xy = DoubleVector(x!!, y!!)
                    if (!result.containsKey(xy)) {
                        result[xy] = MutableDouble(0.0)
                    }
                    result[xy]!!.getAndAdd(SeriesUtil.asFinite(weight[i], 0.0))
                }
            }
            return result
        }
    }
}