/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat.regression

import jetbrains.datalore.plot.base.stat.math3.Percentile
import kotlin.random.Random

internal object RegressionUtil {

    // sample m data randomly
    fun <T> sampling(data: List<T>, m: Int, rnd: () -> Double): ArrayList<T> {
        val index = sampleInt(data.size, m, rnd)
        val result = ArrayList<T>()
        for (i in index) {
            result.add(data[i])
        }
        return result
    }

    // sample m int from 0..n-1
    private fun sampleInt(n: Int, m: Int, rnd: () -> Double): IntArray {
        if (n < m || m < 0) {
            error("Sample $m data from $n data is impossible!")
        }
        val perm = IntArray(n)
        for (i in 0 until n) {
            perm[i] = i
        }

        val result = IntArray(m)
        for (j in 0 until m) {
            val r = j + (rnd() * (n - j)).toInt()
            result[j] = perm[r]
            perm[r] = perm[j]
        }
        return result
    }

    fun percentile(data: List<Double>, p: Double): Double {
        return Percentile.evaluate(data.toDoubleArray(), p * 100)
    }
}
