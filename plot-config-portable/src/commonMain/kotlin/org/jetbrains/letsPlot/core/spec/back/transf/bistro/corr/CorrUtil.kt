/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.back.transf.bistro.corr

import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.data.DataFrameUtil
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil.filterFinite
import kotlin.math.absoluteValue

internal object CorrUtil {
    fun computeCorrelations(
        data: Map<String, List<Any?>>,
        corrFun: (List<Double>, List<Double>) -> Double
    ): Map<Pair<String, String>, Double> {
        val df = DataFrameUtil.fromMap(data)
        val numerics = df.variables().filter { df.isNumeric(it) }
        val correlations = LinkedHashMap<Pair<DataFrame.Variable, DataFrame.Variable>, Double>()

        for (variable in numerics) {
            correlations[variable to variable] = 1.0
        }

        for ((i, vx) in numerics.withIndex()) {
            val xs = df.getNumeric(vx)

            for (j in 0 until i) {
                val vy = numerics[j]
                val ys = df.getNumeric(vy)
                correlations[vx to vy] = correlation(xs, ys, corrFun)
            }
        }

        return correlations.mapKeys {
            Pair(
                it.key.first.label,
                it.key.second.label
            )
        }
    }

    fun correlationsFromCoefficients(
        coeff: Map<String, List<Any?>>
    ): Map<Pair<String, String>, Double> {
        val correlations = LinkedHashMap<Pair<String, String>, Double>()

        coeff.forEach { (vx, vxCoeffs) ->
            coeff.keys.forEachIndexed() { vyIndex, vy ->
                correlations[vx to vy] = vxCoeffs.get(vyIndex) as? Double ?: Double.NaN
            }
        }

        return correlations
    }

    private fun correlation(
        xs: List<Double?>,
        ys: List<Double?>,
        corrFun: (List<Double>, List<Double>) -> Double
    ): Double {
        val filtered = filterFinite(xs, ys)

        @Suppress("NAME_SHADOWING")
        val xs = filtered[0]

        @Suppress("NAME_SHADOWING")
        val ys = filtered[1]

        return corrFun(xs, ys)
    }


    fun matrixXYSeries(
        correlations: Map<Pair<String, String>, Double>,
        variablesInOrder: List<String>,
        type: String,
        nullDiag: Boolean,
        threshold: Double,
        dropDiagNA: Boolean,
        dropOtherNA: Boolean
    ): Pair<List<String>, List<String>> {

        val xs = ArrayList<String>()
        val ys = ArrayList<String>()
        val cm = CorrMatrix(
            correlations,
            nullDiag = nullDiag,
            threshold
        )
        for ((ix, x) in variablesInOrder.withIndex()) {
            val iterY = when (type) {
                "upper" -> variablesInOrder.subList(ix, variablesInOrder.size)
                "lower" -> variablesInOrder.subList(0, ix + 1)
                else -> variablesInOrder
            }
            for (y in iterY) {
                val v = cm.value(x, y)

                if (v == null) {
                    if (dropDiagNA && x == y) continue
                    if (dropOtherNA && x != y) continue
                }

                xs.add(x)
                ys.add(y)
            }
        }

        return Pair(xs, ys)
    }

    fun correlationsToDataframe(
        cm: CorrMatrix,
        xSeries: List<String>,
        ySeries: List<String>,
    ): Map<String, List<Any?>> {

        val corrX = ArrayList<String>()
        val corrY = ArrayList<String>()
        val corr = ArrayList<Double?>()
        val corrAbs = ArrayList<Double?>()

        for ((x, y) in xSeries.zip(ySeries)) {
            // drop all n/a
            val v = cm.value(x, y) ?: continue

            corrX.add(x)
            corrY.add(y)
            corr.add(v)
            corrAbs.add(v.absoluteValue)
        }

        return mapOf<String, List<Any?>>(
            CorrVar.X to corrX,
            CorrVar.Y to corrY,
            CorrVar.CORR to corr,
            CorrVar.CORR_ABS to corrAbs
        )
    }

    internal class CorrMatrix(
        correlations: Map<Pair<String, String>, Double>,
        private val nullDiag: Boolean,
        private val threshold: Double
    ) {
        private val correlations = correlations.mapKeys { toKey(it.key) }

        private fun toKey(s0: String, s1: String): Pair<String, String> {
            return if (s0 < s1) {
                s0 to s1
            } else {
                s1 to s0
            }
        }

        private fun toKey(pair: Pair<String, String>): Pair<String, String> = toKey(pair.first, pair.second)

        fun value(x: String, y: String): Double? {
            return if (x == y && nullDiag) {
                null
            } else {
                val v = correlations[toKey(x, y)]
                when {
                    v == null -> null
                    v.absoluteValue < threshold -> null
                    else -> v
                }
            }
        }
    }
}