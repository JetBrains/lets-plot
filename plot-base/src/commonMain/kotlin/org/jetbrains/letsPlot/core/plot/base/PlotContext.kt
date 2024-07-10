/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base

import org.jetbrains.letsPlot.commons.interval.DoubleSpan

interface PlotContext {
    val superscriptExponent: Boolean

    fun hasScale(aes: Aes<*>): Boolean
    fun getScale(aes: Aes<*>): Scale
    fun overallTransformedDomain(aes: Aes<*>): DoubleSpan

    fun getTooltipFormatter(aes: Aes<*>): (Any?) -> String

    // Simple formatter, based on the bound variable type (e.g. int -> "d", float -> "f", datetime -> "%d.%m.%y %H:%M:%S")
    // If type is not known, returns Any::toString
    fun getDefaultFormatter(aes: Aes<*>): (Any) -> String

    // Simple formatter, based on the variable type (e.g. int -> "d", float -> "f", datetime -> "%d.%m.%y %H:%M:%S")
    // If type is not known, returns Any::toString
    fun getDefaultFormatter(varName: String): (Any) -> String
}

object NullPlotContext : PlotContext {
    override val superscriptExponent: Boolean = false

    override fun hasScale(aes: Aes<*>): Boolean = false
    override fun getScale(aes: Aes<*>): Scale = error("No scale for aesthetic $aes")
    override fun overallTransformedDomain(aes: Aes<*>): DoubleSpan = error("No domain for aesthetic $aes")
    override fun getTooltipFormatter(aes: Aes<*>): (Any?) -> String = Any?::toString
    override fun getDefaultFormatter(aes: Aes<*>): (Any) -> String = Any::toString
    override fun getDefaultFormatter(varName: String): (Any) -> String = Any::toString
}