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

fun allFinite( xs : List<Double?>, ys : List<Double? >) : Pair<DoubleArray, DoubleArray >  {
    var tx = ArrayList<Double>()
    var ty = ArrayList<Double>()

    for ( p in xs.asSequence().zip(ys.asSequence() )) {
        if (  SeriesUtil.allFinite(p)  ) {
            tx.add(p.first!!)
            ty.add(p.second!!)
        }
    }

    return Pair( tx.toDoubleArray(), ty.toDoubleArray() )
}

fun allFiniteUnique( xs : List<Double?>, ys : List<Double? >) : Pair<DoubleArray, DoubleArray >  {
    val tp = ArrayList<Pair<Double,Double>>()

    for ( p in xs.asSequence().zip(ys.asSequence() )) {
        if (  SeriesUtil.allFinite(p)  ) {
            tp.add( Pair( p.first!!, p.second!! ))
        }
    }

    tp.sortBy { it.first }

    if ( tp.isEmpty() )
        return Pair( DoubleArray(0), DoubleArray(0) )

    var tx = ArrayList<Double>()
    var ty = ArrayList<Double>()
    var prev_x = tp.first().first
    var prev_ys = arrayListOf (tp.first().second )

    for ( p in tp.subList(1, tp.size) ) {
        if ( p.first != prev_x ) {
            tx.add(prev_x)
            ty.add( prev_ys.average() )
            prev_x = p.first
            prev_ys.clear()
        }

        prev_ys.add( p.second )
    }

    tx.add(prev_x)
    ty.add( prev_ys.average() )

    return Pair(tx.toDoubleArray(),ty.toDoubleArray())
}
