/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.StatContext
import jetbrains.datalore.plot.base.data.TransformVar
import jetbrains.datalore.plot.common.data.SeriesUtil

class QQ2Stat : BaseStat(DEF_MAPPING) {

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
        val (finiteX, finiteY) = (xs zip ys).filter { (x, y) ->
            SeriesUtil.allFinite(x, y)
        }.unzip()
        val statX = finiteX.map { it!! }.sorted()
        val statY = finiteY.map { it!! }.sorted()

        return mutableMapOf(
            Stats.X to statX,
            Stats.Y to statY
        )
    }

    companion object {
        private val DEF_MAPPING: Map<Aes<*>, DataFrame.Variable> = mapOf(
            Aes.X to Stats.X,
            Aes.Y to Stats.Y
        )
    }
}