/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.StatContext
import jetbrains.datalore.plot.base.data.TransformVar
import jetbrains.datalore.plot.base.stat.regression.LinearRegression

class ResidualStat : BaseStat(DEF_MAPPING) {

    override fun consumes(): List<Aes<*>> {
        return listOf(Aes.X, Aes.Y)
    }

    override fun apply(data: DataFrame, statCtx: StatContext, messageConsumer: (s: String) -> Unit): DataFrame {
        if (!hasRequiredValues(data, Aes.X) || !hasRequiredValues(data, Aes.Y)) {
            return withEmptyStatValues()
        }

        val statData = buildStat(
            data.getNumeric(TransformVar.X),
            data.getNumeric(TransformVar.Y)
        )

        return DataFrame.Builder()
            .putNumeric(Stats.X, statData.getValue(Stats.X))
            .putNumeric(Stats.Y, statData.getValue(Stats.Y))
            .build()
    }

    private fun buildStat(
        xs: List<Double?>,
        ys: List<Double?>
    ): MutableMap<DataFrame.Variable, List<Double>> {
        val filteredData = (xs zip ys).filter {
            it.first?.isFinite() ?: false && it.second?.isFinite() ?: false
        }.map { Pair(it.first!!, it.second!!) }
        val reg = LinearRegression(xs, ys, DEF_CONFIDENCE_LEVEL)
        return mutableMapOf(
            Stats.X to filteredData.unzip().first,
            Stats.Y to filteredData.map { p -> p.second - reg.evalX(p.first).y }
        )
    }

    companion object {
        private const val DEF_CONFIDENCE_LEVEL = 0.99 // Any acceptable value
        private val DEF_MAPPING: Map<Aes<*>, DataFrame.Variable> = mapOf(
            Aes.X to Stats.X,
            Aes.Y to Stats.Y
        )
    }
}