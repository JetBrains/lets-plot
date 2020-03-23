/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat.regression

import jetbrains.datalore.plot.base.stat.math3.Percentile
import jetbrains.datalore.plot.common.data.SeriesUtil
import kotlin.random.Random

internal object RegressionUtil {

    // sample m data randomly
    fun <T> sampling(data: List<T>, m: Int): ArrayList<T> {
        val index = sampleInt(data.size, m)
        val result = ArrayList<T>()
        for (i in index) {
            result.add(data[i])
        }
        return result
    }

    // sample m int from 0..n-1
    private fun sampleInt(n: Int, m: Int): IntArray {
        if (n < m || m < 0) {
            error("Sample $m data from $n data is impossible!")
        }
        val perm = IntArray(n)
        for (i in 0 until n) {
            perm[i] = i
        }

        val result = IntArray(m)
        for (j in 0 until m) {
            val r = j + (Random.nextDouble() * (n - j)).toInt()
            result[j] = perm[r]
            perm[r] = perm[j]
        }
        return result
    }

    fun percentile(data: List<Double>, p: Double): Double {
        return Percentile.evaluate(data.toDoubleArray(), p * 100)
    }
}

fun allFinite(xs: List<Double?>, ys: List<Double?>): Pair<DoubleArray, DoubleArray> {
    val tx = ArrayList<Double>()
    val ty = ArrayList<Double>()

    for ((x, y) in xs.asSequence().zip(ys.asSequence())) {
        if (SeriesUtil.allFinite(x, y)) {
            tx.add(x!!)
            ty.add(y!!)
        }
    }

    return Pair(tx.toDoubleArray(), ty.toDoubleArray())
}

fun allFiniteUnique(xs: List<Double?>, ys: List<Double?>): Pair<DoubleArray, DoubleArray> {
    val tp = ArrayList<Pair<Double, Double>>()

    for ((x, y) in xs.asSequence().zip(ys.asSequence())) {
        if (SeriesUtil.allFinite(x, y)) {
            tp.add(Pair(x!!, y!!))
        }
    }

    tp.sortBy { it.first }

    if (tp.isEmpty())
        return Pair(DoubleArray(0), DoubleArray(0))

    val tx = ArrayList<Double>()
    val ty = ArrayList<Double>()
    var (prev_x, sumY) = tp.first()
    var countY = 1

    for ((x, y) in tp.subList(1, tp.size)) {
        if (x != prev_x) {
            tx.add(prev_x)
            ty.add(sumY.div(countY))
            prev_x = x
            sumY = 0.0
            countY = 0
        }

        sumY += y
        countY += 1
    }

    tx.add(prev_x)
    ty.add(sumY.div(countY))

    return Pair(tx.toDoubleArray(), ty.toDoubleArray())
}
