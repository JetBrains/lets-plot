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
    val layers: List<Layer>

    fun hasScale(aes: org.jetbrains.letsPlot.core.plot.base.Aes<*>): Boolean
    fun getScale(aes: org.jetbrains.letsPlot.core.plot.base.Aes<*>): Scale
    fun overallTransformedDomain(aes: org.jetbrains.letsPlot.core.plot.base.Aes<*>): DoubleSpan

    fun getTooltipFormatter(aes: org.jetbrains.letsPlot.core.plot.base.Aes<*>, defaultValue: () -> (Any?) -> String): (Any?) -> String

    interface Layer {
        val isLegendDisabled: Boolean
        val aestheticsDefaults: AestheticsDefaults
        val legendKeyElementFactory: LegendKeyElementFactory
        val colorByAes: org.jetbrains.letsPlot.core.plot.base.Aes<Color>
        val fillByAes: org.jetbrains.letsPlot.core.plot.base.Aes<Color>
        fun renderedAes(): List<org.jetbrains.letsPlot.core.plot.base.Aes<*>>
        fun hasBinding(aes: org.jetbrains.letsPlot.core.plot.base.Aes<*>): Boolean
        fun hasConstant(aes: org.jetbrains.letsPlot.core.plot.base.Aes<*>): Boolean
        fun <T> getConstant(aes: org.jetbrains.letsPlot.core.plot.base.Aes<T>): T
    }
}