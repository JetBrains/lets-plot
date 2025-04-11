/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.tooltip

import org.jetbrains.letsPlot.commons.formatting.string.StringFormat
import org.jetbrains.letsPlot.commons.formatting.string.StringFormat.FormatType
import org.jetbrains.letsPlot.core.commons.data.DataType
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.FormatterUtil
import org.jetbrains.letsPlot.core.plot.base.PlotContext
import org.jetbrains.letsPlot.core.plot.base.scale.ScaleUtil
import org.jetbrains.letsPlot.core.plot.base.stat.Stats

internal object TooltipFormatting {
    private val FALLBACK_NUMBER_FORMATTER: (v: Number) -> String =
        StringFormat.forOneArg(".2f", FormatType.NUMBER_FORMAT)::format

    fun fromScale(aes: Aes<*>, ctx: PlotContext): (Any?) -> String {
        // expect only X,Y or not positional
        check(!Aes.isPositionalXY(aes) || aes == Aes.X || aes == Aes.Y) {
            "Positional aesthetic should be either X or Y but was $aes"
        }

        val scale = ctx.getScale(aes)

        val formatter = if (scale.isContinuousDomain) {
            if (scale.userFormatter != null) {
                scale.userFormatter!!
            } else {
                val domain = ctx.overallTransformedDomain(aes)
                scale.getBreaksGenerator().defaultFormatter(domain, 100)
            }
        } else {
            val labelsMap = ScaleUtil.labelByBreak(scale);
//            labelsMap::get

            { v ->
                if (v in labelsMap) {
                    labelsMap.getValue(v)
                } else if (v is Number) {
                    // The case when numeric data is mapped to discrete axis.
                    FALLBACK_NUMBER_FORMATTER(v)
                } else {
                    null
                }
            }
        }

        return { value ->
            value?.let {
                formatter.invoke(it)
            } ?: "n/a"
        }
    }

    fun createFormatter(
        variable: DataFrame.Variable,
        formatters: Map<Any, (Any) -> String>,
        expFormat: StringFormat.ExponentFormat
    ): (Any) -> String {
        return when (variable) {
            Stats.PROP, Stats.SUMPROP -> StringFormat.forOneArg(
                ".2f",
                formatFor = variable.name,
                expFormat = expFormat
            )::format

            Stats.PROPPCT, Stats.SUMPCT -> StringFormat.forOneArg(
                "{.1f} %",
                formatFor = variable.name,
                expFormat = expFormat
            )::format

            else -> formatters[variable.name] ?: FormatterUtil.byDataType(DataType.UNKNOWN, expFormat)
        }
    }
}