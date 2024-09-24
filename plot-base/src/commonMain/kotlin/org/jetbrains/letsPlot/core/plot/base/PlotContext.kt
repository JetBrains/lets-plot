/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base

import org.jetbrains.letsPlot.commons.formatting.number.NumberFormat.ExponentFormat
import org.jetbrains.letsPlot.commons.interval.DoubleSpan

interface PlotContext {
    val exponentFormat: ExponentFormat

    fun hasScale(aes: Aes<*>): Boolean
    fun getScale(aes: Aes<*>): Scale
    fun overallTransformedDomain(aes: Aes<*>): DoubleSpan

    fun getTooltipFormatter(aes: Aes<*>): (Any?) -> String
}

object NullPlotContext : PlotContext {
    override val exponentFormat: ExponentFormat = ExponentFormat.E

    override fun hasScale(aes: Aes<*>): Boolean = false
    override fun getScale(aes: Aes<*>): Scale = error("No scale for aesthetic $aes")
    override fun overallTransformedDomain(aes: Aes<*>): DoubleSpan = error("No domain for aesthetic $aes")
    override fun getTooltipFormatter(aes: Aes<*>): (Any?) -> String = Any?::toString
}