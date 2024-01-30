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

    fun getTooltipFormatter(aes: Aes<*>, defaultValue: () -> (Any?) -> String): (Any?) -> String
}