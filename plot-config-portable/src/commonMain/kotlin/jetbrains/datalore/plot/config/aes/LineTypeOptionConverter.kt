/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config.aes

import org.jetbrains.letsPlot.commons.intern.function.Function
import jetbrains.datalore.plot.base.render.linetype.LineType
import jetbrains.datalore.plot.base.render.linetype.NamedLineType

internal class LineTypeOptionConverter : Function<Any?, LineType?> {

    override fun apply(value: Any?): LineType {
        /*
     * Line type is specified with either an integer (code 0..6), a name, or with a string of
     * an even number (up to eight) of hexadecimal digits which give the lengths in
     * consecutive positions in the string.
     * <p/>
     * Codes and names:
     * 0 = blank, 1 = solid, 2 = dashed, 3 = dotted, 4 = dotdash, 5 = longdash, 6 = twodash
     */

        if (value == null) {
            return NamedLineType.SOLID
        }
        if (value is LineType) {
            return value
        }
        if (value is String && LINE_TYPE_BY_NAME.containsKey(value)) {
            return LINE_TYPE_BY_NAME[value]!!
        }
        return if (value is Number && LINE_TYPE_BY_CODE.containsKey(value.toInt())) {
            LINE_TYPE_BY_CODE[value.toInt()]!!
        } else NamedLineType.SOLID

        // todo: could be string of hexadecimal digits (not implemented)
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
    }
}
