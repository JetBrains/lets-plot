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
        value is String -> HexLineType.parse(value)
        value is List<*> -> DashedLineType.parse(value)
        else -> NamedLineType.SOLID
    }
}
