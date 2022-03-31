/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat

import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.StatContext
import jetbrains.datalore.plot.base.data.TransformVar
import jetbrains.datalore.plot.common.data.SeriesUtil

class YDensityStat(
    private val scale: Scale,
    private val bandWidth: Double?,
    private val bandWidthMethod: DensityStat.BandWidthMethod,
    private val adjust: Double,
    private val kernel: DensityStat.Kernel,
    private val n: Int,
    private val fullScanMax: Int
) : BaseStat(DEF_MAPPING) {

    init {
        require(n <= DensityStat.MAX_N) {
            "The input n = $n > ${DensityStat.MAX_N} is too large!"
        }
    }

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

        val statData = buildStat(xs, ys, ws)

        val builder = DataFrame.Builder()
        for ((variable, series) in statData) {
            builder.putNumeric(variable, series)
        }
        return builder.build()
    }

    override fun normalize(dataAfterStat: DataFrame): DataFrame {
        val statViolinWidth = if (dataAfterStat.rowCount() == 0) {
            emptyList()
        } else {
            when (scale) {
                Scale.AREA -> {
                    val statDensity = dataAfterStat.getNumeric(Stats.DENSITY).map { it!! }
                    val densityMax = statDensity.maxOrNull()!!
                    statDensity.map { it / densityMax }
                }
                Scale.COUNT -> {
                    val statDensity = dataAfterStat.getNumeric(Stats.DENSITY).map { it!! }
                    val densityMax = statDensity.maxOrNull()!!
                    val statCount = dataAfterStat.getNumeric(Stats.COUNT).map { it!! }
                    val widthsSumMax = statDensity.mapIndexed { i, d ->
                        if (d > 0) statCount[i] / d else Double.NaN
                    }.maxOrNull()!!
                    val norm = densityMax * widthsSumMax
                    statCount.map { it / norm }
                }
                Scale.WIDTH -> {
                    dataAfterStat.getNumeric(Stats.SCALED)
                }
            }
        }
        return dataAfterStat.builder()
            .putNumeric(Stats.VIOLIN_WIDTH, statViolinWidth)
            .build()
    }

    private fun buildStat(
        xs: List<Double?>,
        ys: List<Double?>,
        ws: List<Double?>
    ): MutableMap<DataFrame.Variable, List<Double>> {
        val binnedData = (xs zip (ys zip ws))
            .filter { it.first?.isFinite() == true }
            .groupBy({ it.first!! }, { it.second })
            .mapValues { it.value.unzip() }

        val statX = ArrayList<Double>()
        val statY = ArrayList<Double>()
        val statDensity = ArrayList<Double>()
        val statCount = ArrayList<Double>()
        val statScaled = ArrayList<Double>()

        for ((x, bin) in binnedData) {
            val (filteredY, filteredW) = SeriesUtil.filterFinite(bin.first, bin.second)
            val (binY, binW) = (filteredY zip filteredW)
                .sortedBy { it.first }
                .unzip()
            if (binY.isEmpty()) continue
            val ySummary = FiveNumberSummary(binY)
            val rangeY = DoubleSpan(ySummary.min, ySummary.max)
            val binStatY = DensityStatUtil.createStepValues(rangeY, n)
            val densityFunction = DensityStatUtil.densityFunction(
                binY, binW,
                bandWidth, bandWidthMethod, adjust, kernel, fullScanMax
            )
            val binStatCount = binStatY.map { densityFunction(it) }
            val widthsSum = binW.sum()
            val maxBinCount = binStatCount.maxOrNull()!!

            statX += MutableList(binStatY.size) { x }
            statY += binStatY
            statDensity += binStatCount.map { it / widthsSum }
            statCount += binStatCount
            statScaled += binStatCount.map { it / maxBinCount }
        }

        return mutableMapOf(
            Stats.X to statX,
            Stats.Y to statY,
            Stats.DENSITY to statDensity,
            Stats.COUNT to statCount,
            Stats.SCALED to statScaled
        )
    }

    enum class Scale {
        AREA,
        COUNT,
        WIDTH
    }

    companion object {
        val DEF_SCALE = Scale.AREA

        private val DEF_MAPPING: Map<Aes<*>, DataFrame.Variable> = mapOf(
            Aes.X to Stats.X,
            Aes.Y to Stats.Y,
            Aes.VIOLINWIDTH to Stats.VIOLIN_WIDTH
        )
    }
}