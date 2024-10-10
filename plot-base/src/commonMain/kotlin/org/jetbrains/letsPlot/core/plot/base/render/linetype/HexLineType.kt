/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.linetype

internal class HexLineType(myDashArray: List<Double>) : LineType{

    override val isSolid: Boolean = false

    override val dashArray: List<Double> = myDashArray

    override val dashOffset: Double = 0.0

    companion object {
        fun parse(value: String): LineType {
            //  string of an even number (up to eight) of hexadecimal digits
            //  which give the lengths in consecutive positions in the string
            require(value.length % 2 == 0 && value.length <= 8) {
                "The option 'linetype' requires a string of an even number (up to eight) of hexadecimal digits, " +
                        "but was: $value." }

            val dashArray = value.map { it.toString().toInt(16).toDouble() }
            return HexLineType(dashArray)
        }
    }
}