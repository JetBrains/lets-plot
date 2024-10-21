/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.plotson

import org.jetbrains.letsPlot.core.spec.Option
import org.jetbrains.letsPlot.core.spec.StatKind

open class StatOptions(statKind: StatKind): InlineOptions() {
    var kind: StatKind? by map(Option.Layer.STAT)

    init {
        kind = statKind
    }
}

class SummaryStatOptions(): StatOptions(StatKind.SUMMARY) {
    var f: AggFunction? by map(Option.Stat.Summary.FUN)
    var fMin: AggFunction? by map(Option.Stat.Summary.FUN_MIN)
    var fMax: AggFunction? by map(Option.Stat.Summary.FUN_MAX)

    enum class AggFunction(
        val value: String
    ) {
        COUNT(Option.Stat.Summary.Functions.COUNT),
        SUM(Option.Stat.Summary.Functions.SUM),
        MEAN(Option.Stat.Summary.Functions.MEAN),
        MEDIAN(Option.Stat.Summary.Functions.MEDIAN),
        MIN(Option.Stat.Summary.Functions.MIN),
        MAX(Option.Stat.Summary.Functions.MAX),
        LQ(Option.Stat.Summary.Functions.LQ),
        MQ(Option.Stat.Summary.Functions.MQ),
        UQ(Option.Stat.Summary.Functions.UQ),
    }
}

class DensityStatOptions(): StatOptions(StatKind.DENSITY) {
    var n: Int? by map(Option.Stat.Density.N)
    var kernel: String? by map(Option.Stat.Density.KERNEL)
    var bandWidth: Any? by map(Option.Stat.Density.BAND_WIDTH)
    var adjust: Double? by map(Option.Stat.Density.ADJUST)
    var fullScanMax: Int? by map(Option.Stat.Density.FULL_SCAN_MAX)
    var trim: Boolean? by map(Option.Stat.Density.TRIM)
    var quantiles: List<Double>? by map(Option.Stat.Density.QUANTILES)
}

class BinStatOptions(): StatOptions(StatKind.BIN) {
    var threshold: Double? by map(Option.Stat.Bin.THRESHOLD)
    var bins: Int? by map(Option.Stat.Bin.BINS)
    var binWidth: Double? by map(Option.Stat.Bin.BINWIDTH)
    var center: Double? by map(Option.Stat.Bin.CENTER)
    var boundary: Double? by map(Option.Stat.Bin.BOUNDARY)
}

fun identityStat() = StatOptions(StatKind.IDENTITY)
fun countStat() = StatOptions(StatKind.COUNT)
fun boxplotOutlierStat() = StatOptions(StatKind.BOXPLOT_OUTLIER)
fun binStat(block: BinStatOptions.() -> Unit = {}) = BinStatOptions().apply(block)
fun summaryStat(block: SummaryStatOptions.() -> Unit = {}) = SummaryStatOptions().apply(block)
fun densityStat(block: DensityStatOptions.() -> Unit = {}) = DensityStatOptions().apply(block)