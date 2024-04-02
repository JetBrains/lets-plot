/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.linetype

enum class NamedLineType(val code: Int, private val myDashArray: List<Double>?) :
    LineType {
    // 0 = blank, 1 = solid, 2 = dashed, 3 = dotted, 4 = dotdash, 5 = longdash, 6 = twodash
    BLANK(0, null) {
        override val isBlank: Boolean
            get() = true
    },
    SOLID(1, null) {
        override val isSolid: Boolean
            get() = true
    },
    DASHED(2, listOf(4.3, 4.3)),
    DOTTED(3, listOf(1.0, 3.2)),
    @Suppress("SpellCheckingInspection")
    DOTDASH(4, listOf(1.0, 3.2, 4.3, 3.2)),
    @Suppress("SpellCheckingInspection")
    LONGDASH(5, listOf(7.4, 3.2)),
    @Suppress("SpellCheckingInspection")
    TWODASH(6, listOf(2.4, 2.4, 6.4, 2.4));

    override val isSolid: Boolean
        get() = false

    override val isBlank: Boolean
        get() = false

    override val dashArray: List<Double>
        get() {
            if (!(isSolid || isBlank)) {
                return myDashArray!!
            }
            throw IllegalStateException("No dash array in " + name.lowercase() + " linetype")
        }

    companion object {
        private val LINE_TYPE_BY_CODE = HashMap<Int, NamedLineType>()
        private val LINE_TYPE_BY_NAME = HashMap<String, NamedLineType>()

        init {
            for (lineType in NamedLineType.values()) {
                LINE_TYPE_BY_CODE[lineType.code] = lineType
                LINE_TYPE_BY_NAME[lineType.name.lowercase()] = lineType
            }
        }

        fun parse(value: Any?): LineType {
            /*
            * Line type is specified with either an integer (code 0..6), a name, or with a string of
            * an even number (up to eight) of hexadecimal digits which give the lengths in
            * consecutive positions in the string.
            * <p/>
            * Codes and names:
            * 0 = blank, 1 = solid, 2 = dashed, 3 = dotted, 4 = dotdash, 5 = longdash, 6 = twodash
            */
            return when {
                value == null -> SOLID
                value is LineType -> value
                value is String && LINE_TYPE_BY_NAME.containsKey(value) -> LINE_TYPE_BY_NAME[value]!!
                value is Number && LINE_TYPE_BY_CODE.containsKey(value.toInt()) -> LINE_TYPE_BY_CODE[value.toInt()]!!
                else -> SOLID

                // todo: could be string of hexadecimal digits (not implemented)
            }
        }
    }
}
