/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.tooltip

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.PlotContext
import org.jetbrains.letsPlot.core.plot.base.tooltip.FormatterProvider

class TooltipFormatterProvider(private val plotContext: PlotContext): FormatterProvider {

    private val myAesFormatters: MutableMap<Aes<*>, (Any?) -> String> = HashMap()

    override fun getFormatter(aes: Aes<*>): (Any?) -> String {
        return myAesFormatters.getOrPut(aes) { createFormatter(aes) }
    }

    private fun createFormatter(aes: Aes<*>): (Any?) -> String {
        return if (plotContext.hasScale(aes)) {
            TooltipFormatting.createFormatter(aes, plotContext)
        } else {
            Any?::toString
        }
    }
}