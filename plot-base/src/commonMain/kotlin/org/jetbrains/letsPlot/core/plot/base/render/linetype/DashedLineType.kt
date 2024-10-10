/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.linetype


internal class DashedLineType(myDashArray: List<Double>, offset: Double) : LineType {
    override val isSolid: Boolean = false

    override val dashArray: List<Double> = myDashArray

    override val dashOffset: Double = offset

    companion object {
        // input array: [dash, gap, ...] or (offset, [dash, gap, ...])
        fun parse(value: List<*>): LineType {
            fun parseDashArray(v: List<*>): List<Double> {
                require(v.all { it is Number }) { "The option 'linetype' requires a list of numbers, but was: $v." }
                return v.map { (it as Number).toDouble() }
            }

            return if (value.size == 2 && value[0] is Number && value[1] is List<*>) {
                DashedLineType(parseDashArray(value[1] as List<*>), offset = (value[0] as Number).toDouble())
            } else {
                DashedLineType(parseDashArray(value), offset = 0.0)
            }
        }
    }
}