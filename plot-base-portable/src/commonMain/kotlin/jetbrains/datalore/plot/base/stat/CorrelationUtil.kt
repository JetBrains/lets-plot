/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat

import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.data.DataFrameUtil.isNumeric
import jetbrains.datalore.plot.base.stat.regression.allFinite
import kotlin.math.abs

object CorrelationUtil {

    fun correlation(
        lxs: List<Double?>, lys: List<Double?>,
        corrfn: (DoubleArray, DoubleArray) -> Double
    ): Double {
        val (xs, ys) = allFinite(lxs, lys)
        return corrfn(xs, ys)
    }

    fun correlationMatrix(
        data: DataFrame,
        type: CorrelationStat.Type,
        fillDiagonal: Boolean,
        corrfn: (DoubleArray, DoubleArray) -> Double,
        threshold: Double = CorrelationStat.DEF_THRESHOLD
    ): DataFrame {
        val numerics = data.variables().filter { isNumeric(data, it.name) }

        val var1: ArrayList<String> = arrayListOf()
        val var2: ArrayList<String> = arrayListOf()
        val corr: ArrayList<Double?> = arrayListOf()

        fun addCorrelation(varX: String, varY: String, v: Double) {
            if (abs(v) >= threshold) {
                var1.add(varX)
                var2.add(varY)
                corr.add(v)
            }
        }

        for ((i, vx) in numerics.withIndex()) {

            if (fillDiagonal) {
                addCorrelation(
                    vx.label,
                    vx.label,
                    1.0
                )
            }

            val xs = data.getNumeric(vx)

            for (j in 0 until i) {
                val vy = numerics[j]
                val ys = data.getNumeric(vy)
                val c = correlation(xs, ys, corrfn)

                if (type == CorrelationStat.Type.FULL || type == CorrelationStat.Type.LOWER) {
                    addCorrelation(vx.label, vy.label, c )
                }

                if (type == CorrelationStat.Type.FULL || type == CorrelationStat.Type.UPPER) {
                    addCorrelation(vy.label, vx.label, c )
                }
            }
        }

        return DataFrame.Builder()
            .putDiscrete(Stats.X, var1)
            .putDiscrete(Stats.Y, var2)
            .putNumeric(Stats.CORR, corr)
            .build()
    }
}