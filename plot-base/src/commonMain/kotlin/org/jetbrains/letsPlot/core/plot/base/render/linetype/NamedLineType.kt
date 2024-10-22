/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.linetype

enum class NamedLineType(val code: Int, private val myDashArray: List<Double>?) :
    LineType {
    // 0 = blank, 1 = solid, 2 = dashed, 3 = dotted, 4 = dotdash, 5 = longdash, 6 = twodash
    BLANK(0, listOf(0.0, 1.0)),
    SOLID(1, null) {
        override val isSolid: Boolean
            get() = true
    },
    DASHED(2, listOf(4.0, 4.0)),
    DOTTED(3, listOf(1.0, 3.0)),
    @Suppress("SpellCheckingInspection")
    DOTDASH(4, listOf(1.0, 3.0, 4.0, 3.0)),
    @Suppress("SpellCheckingInspection")
    LONGDASH(5, listOf(7.0, 3.0)),
    @Suppress("SpellCheckingInspection")
    TWODASH(6, listOf(2.0, 2.0, 6.0, 2.0));

    override val isSolid: Boolean
        get() = false

    override val dashArray: List<Double>
        get() {
            if (!isSolid) {
                return myDashArray!!
            }
            throw IllegalStateException("No dash array in " + name.lowercase() + " linetype")
        }

    override val dashOffset: Double = 0.0
}

private val LINE_TYPE_BY_CODE = NamedLineType.entries.associateBy { it.code }
private val LINE_TYPE_BY_NAME = NamedLineType.entries.associateBy { it.name.lowercase() }


private class CustomLineType(myDashArray: List<Double>, offset: Double) : LineType {
    override val isSolid: Boolean = false

    override val dashArray: List<Double> = myDashArray

    override val dashOffset: Double = offset
}

fun parse(value: Any?): LineType {
    /*
    * The line type is specified by either an integer (code 0..6) or a name
    * or by list to define custom linetypes with dashoffset/dasharray.
    * - Codes and names:
    *   0 = blank, 1 = solid, 2 = dashed, 3 = dotted, 4 = dotdash, 5 = longdash, 6 = twodash
    * - List: [dash, gap, ...] or (offset, [dash, gap, ...]).
    * - String of hexadecimal digits
    */
    return when {
        value == null -> NamedLineType.SOLID
        value is LineType -> value
        value is String && LINE_TYPE_BY_NAME.containsKey(value) -> LINE_TYPE_BY_NAME[value]!!
        value is Number && LINE_TYPE_BY_CODE.containsKey(value.toInt()) -> LINE_TYPE_BY_CODE[value.toInt()]!!
        value is String -> {
            require(value.length % 2 == 0 && value.length <= 8) {
                "The option 'linetype' requires a string of an even number (up to eight) of hexadecimal digits, " +
                        "but was: $value." }
            val dashArray = value.map { it.toString().toInt(16).toDouble() }
            CustomLineType(dashArray, offset = 0.0)
        }
        value is List<*> -> {
            fun parseDashArray(v: List<*>): List<Double> {
                require(v.all { it is Number }) { "The option 'linetype' requires a list of numbers, but was: $v." }
                return v.map { (it as Number).toDouble() }
            }
            return if (value.size == 2 && value[0] is Number && value[1] is List<*>) {
                CustomLineType(parseDashArray(value[1] as List<*>), offset = (value[0] as Number).toDouble())
            } else {
                CustomLineType(parseDashArray(value), offset = 0.0)
            }
        }
        else -> NamedLineType.SOLID
    }
}
