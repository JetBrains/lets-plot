/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config.aes

import org.jetbrains.letsPlot.commons.intern.function.Function
import org.jetbrains.letsPlot.core.plot.base.render.point.NamedShape
import org.jetbrains.letsPlot.core.plot.base.render.point.PointShape
import org.jetbrains.letsPlot.core.plot.base.render.point.TinyPointShape

internal class ShapeOptionConverter : Function<Any?, PointShape?> {

    override fun apply(value: Any?): PointShape? {
        return convert(value)
    }

    companion object {
        private val SHAPE_BY_CODE: Map<Int, PointShape>

        init {
            val map = HashMap<Int, PointShape>()
            for (shape in NamedShape.values()) {
                map[shape.code] = shape
            }
            map[TinyPointShape.code] =
                TinyPointShape
            SHAPE_BY_CODE = map
        }

        /**
         * @param value - integer [0..25] : codes of NamedShape
         * - '.' or code 46 : smallest rectangle (1px)
         * - A single character (or char code) : use this char as plotting symbol
         */
        private fun convert(value: Any?): PointShape? {
            if (value == null) {
                return null
            }

            if (value is PointShape) {
                return value
            }
            return if (value is Number && SHAPE_BY_CODE.containsKey(value.toInt())) {
                SHAPE_BY_CODE[value.toInt()]!!
            } else charShape(value.toString())
        }

        private fun charShape(s: String): PointShape {
            if (s.isNotEmpty()) {
                val ch = s[0]
                return if (ch == '.') {
                    TinyPointShape
                } else NamedShape.BULLET
                // TODO: implement
            }
            return TinyPointShape
        }
    }
}
