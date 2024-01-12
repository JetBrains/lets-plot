/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.aes.AestheticsDefaults
import org.jetbrains.letsPlot.core.plot.base.render.LegendKeyElementFactory

interface PlotContext {
    val superscriptExponent: Boolean
    val layers: List<Layer>

    fun hasScale(aes: Aes<*>): Boolean
    fun getScale(aes: Aes<*>): Scale
    fun overallTransformedDomain(aes: Aes<*>): DoubleSpan

    fun getTooltipFormatter(aes: Aes<*>, defaultValue: () -> (Any?) -> String): (Any?) -> String

    interface Layer {
        val isLegendDisabled: Boolean
        val aestheticsDefaults: AestheticsDefaults
        val legendKeyElementFactory: LegendKeyElementFactory
        val colorByAes: Aes<Color>
        val fillByAes: Aes<Color>
        fun renderedAes(): List<Aes<*>>
        fun hasBinding(aes: Aes<*>): Boolean
        fun hasConstant(aes: Aes<*>): Boolean
        fun <T> getConstant(aes: Aes<T>): T
    }
}