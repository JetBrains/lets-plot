/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.annotation

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.PlotContext
import org.jetbrains.letsPlot.core.plot.builder.tooltip.TooltipFormatting

internal object AnnotationFormatting {

    fun createFormatter(aes: Aes<*>, ctx: PlotContext): (Any?) -> String {
        return if (ctx.hasScale(aes) && ctx.getScale(aes).isContinuousDomain) {
            ctx.getTooltipFormatter(aes) {
                TooltipFormatting.createFormatter(aes, ctx)
            }
        } else {
            { v: Any? -> v.toString() }
        }
    }
}