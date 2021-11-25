/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.StatContext
import jetbrains.datalore.plot.base.data.TransformVar

class ViolinStat : BaseStat(DEF_MAPPING) {

    // TODO: Try to replace by XMIN, XMAX and Y
    override fun consumes(): List<Aes<*>> {
        return listOf(Aes.X, Aes.Y, Aes.WEIGHT)
    }

    override fun apply(data: DataFrame, statCtx: StatContext, messageConsumer: (s: String) -> Unit): DataFrame {
        if (!hasRequiredValues(data, Aes.Y)) {
            return withEmptyStatValues()
        }

        val ys = data.getNumeric(TransformVar.Y)
        val xs = if (data.has(TransformVar.X)) {
            data.getNumeric(TransformVar.X)
        } else {
            List(ys.size) { 0.0 }
        }
        val ws = if (data.has(TransformVar.WEIGHT)) {
            data.getNumeric(TransformVar.WEIGHT)
        } else {
            List(ys.size) { 1.0 }
        }

        val statData = buildStat(xs, ys, ws, messageConsumer)

        val builder = DataFrame.Builder()
        for ((variable, series) in statData) {
            builder.putNumeric(variable, series)
        }
        return builder.build()
    }

    companion object {
        private val DEF_MAPPING: Map<Aes<*>, DataFrame.Variable> = mapOf(
            Aes.X to Stats.X,
            Aes.Y to Stats.Y,
            Aes.WEIGHT to Stats.DENSITY,
        )

        private fun buildStat(
            xs: List<Double?>,
            ys: List<Double?>,
            ws: List<Double?>,
            messageConsumer: (s: String) -> Unit
        ): MutableMap<DataFrame.Variable, List<Double>> {
            val binnedData = (xs zip (ys zip ws))
                .groupBy({ it.first!! }, { it.second })
                .mapValues { it.value.unzip() }

            val statX = ArrayList<Double>()
            val statY = ArrayList<Double>()
            val statDensity = ArrayList<Double>()

            for ((x, bin) in binnedData) {
                val statData = buildBinStat(bin.first, bin.second, messageConsumer)
                statX += MutableList(statData.getValue(Stats.Y).size) { x }
                statY += statData.getValue(Stats.Y)
                statDensity += statData.getValue(Stats.DENSITY)
            }

            return mutableMapOf(
                Stats.X to statX,
                Stats.Y to statY,
                Stats.DENSITY to statDensity
            )
        }

        private fun buildBinStat(
            binY: List<Double?>,
            binW: List<Double?>,
            messageConsumer: (s: String) -> Unit
        ): MutableMap<DataFrame.Variable, List<Double>> {
            // TODO: Replace defaults by params
            val stat = DensityStat(
                bandWidth = DensityStatUtil.bandWidth(DensityStat.DEF_BW, binY),
                bandWidthMethod = DensityStat.DEF_BW,
                adjust = DensityStat.DEF_ADJUST,
                kernel = DensityStat.DEF_KERNEL,
                n = DensityStat.DEF_N,
                fullScalMax = DensityStat.DEF_FULL_SCAN_MAX
            )
            val data = DataFrame.Builder()
                .putNumeric(TransformVar.X, binY)
                .putNumeric(TransformVar.WEIGHT, binW)
                .build()
            val statData = stat.apply(data, SimpleStatContext(data), messageConsumer)

            return mutableMapOf(
                Stats.Y to statData.getNumeric(Stats.X).requireNoNulls(),
                Stats.DENSITY to statData.getNumeric(Stats.DENSITY).requireNoNulls()
            )
        }
    }
}