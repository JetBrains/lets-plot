/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat.regression

import jetbrains.datalore.plot.base.stat.math3.Percentile
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil
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

fun sumOfSquaredDeviations(xVals: DoubleArray, meanX: Double): Double {
    return sumOfDeviationProducts(xVals, xVals, meanX, meanX)
}

fun sumOfDeviationProducts(xVals: DoubleArray, yVals: DoubleArray, meanX: Double, meanY: Double): Double {
    return (xVals zip yVals).sumOf { (x, y) -> (x - meanX) * (y - meanY) }
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

private fun finitePairs(xs: List<Double?>, ys: List<Double?>): ArrayList<Pair<Double, Double>> {
    val res = ArrayList<Pair<Double, Double>>()

    for ((x, y) in xs.asSequence().zip(ys.asSequence())) {
        if (SeriesUtil.allFinite(x, y)) {
            res.add(Pair(x!!, y!!))
        }
    }

    return res
}

private fun averageByX(lst: List<Pair<Double, Double>>): Pair<List<Double>, List<Double>> {

    if (lst.isEmpty())
        return Pair(ArrayList<Double>(), ArrayList<Double>())

    val tx = ArrayList<Double>()
    val ty = ArrayList<Double>()
    var (prevX, sumY) = lst.first()
    var countY = 1

    for ((x, y) in lst.asSequence().drop(1)) {
        if (x == prevX) {
            sumY += y
            ++countY
        } else {
            tx.add(prevX)
            ty.add(sumY.div(countY))
            prevX = x
            sumY = y
            countY = 1
        }
    }

    tx.add(prevX)
    ty.add(sumY.div(countY))

    return Pair(tx, ty)
}

fun averageByX(xs: List<Double?>, ys: List<Double?>): Pair<DoubleArray, DoubleArray> {
    val tp = finitePairs(xs, ys)
    tp.sortBy { it.first }
    val res = averageByX(tp)
    return Pair(res.first.toDoubleArray(), res.second.toDoubleArray())
}
