/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.plotson

import org.jetbrains.letsPlot.core.spec.Option
import org.jetbrains.letsPlot.core.spec.StatKind

open class StatOptions(statKind: StatKind, holder: Options): InlineOptions(holder.properties) {
    var kind: StatKind? by map(Option.Layer.STAT)

    init {
        kind = statKind
    }
}


class SummaryStatOptions(holder: Options): StatOptions(StatKind.SUMMARY, holder) {
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

fun LayerOptions.identityStat() = StatOptions(StatKind.IDENTITY, this)
fun LayerOptions.countStat() = StatOptions(StatKind.COUNT, this)
fun LayerOptions.boxplotOutlierStat() = StatOptions(StatKind.BOXPLOT_OUTLIER, this)

fun LayerOptions.summaryStat(block: SummaryStatOptions.() -> Unit): SummaryStatOptions {
    return SummaryStatOptions(this).apply(block)
}
