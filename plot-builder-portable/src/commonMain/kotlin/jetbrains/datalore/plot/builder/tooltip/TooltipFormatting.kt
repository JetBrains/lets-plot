/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.tooltip

import org.jetbrains.letsPlot.commons.formatting.string.StringFormat
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.PlotContext
import jetbrains.datalore.plot.base.scale.ScaleUtil
import jetbrains.datalore.plot.base.stat.Stats

internal object TooltipFormatting {
    fun createFormatter(aes: Aes<*>, ctx: PlotContext): (Any?) -> String {
        // expect only X,Y or not positional
        check(!Aes.isPositionalXY(aes) || aes == Aes.X || aes == Aes.Y) {
            "Positional aesthetic should be either X or Y but was $aes"
        }

        val scale = ctx.getScale(aes)
        if (scale.isContinuousDomain) {
            val domain = ctx.overallTransformedDomain(aes)
            val formatter = scale.getBreaksGenerator().defaultFormatter(domain, 100)
            return { value -> value?.let { formatter.invoke(it) } ?: "n/a" }
        } else {
            val labelsMap = ScaleUtil.labelByBreak(scale)
            return { value -> value?.let { labelsMap[it] } ?: "n/a" }
        }
    }

    fun createFormatter(variable: DataFrame.Variable): (Any) -> String {
        return when (variable) {
            Stats.PROP -> StringFormat.forOneArg(".2f", formatFor = variable.name)::format
            Stats.PROPPCT -> StringFormat.forOneArg("{.1f} %", formatFor = variable.name)::format
            else -> { value -> value.toString() }
        }
    }
}