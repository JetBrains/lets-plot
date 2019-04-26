package jetbrains.datalore.visualization.plot.gog.config.aes

import jetbrains.datalore.base.function.Function
import jetbrains.datalore.visualization.plot.gog.core.render.point.NamedShape
import jetbrains.datalore.visualization.plot.gog.core.render.point.PointShape
import jetbrains.datalore.visualization.plot.gog.core.render.point.PointShapes
import observable.collections.Collections

internal class ShapeOptionConverter : Function<Any?, PointShape?> {

    override fun apply(option: Any?): PointShape {
        return convert(option)
    }

    companion object {
        private val SHAPE_BY_CODE: Map<Int, PointShape>

        init {
            val map = HashMap<Int, PointShape>()
            for (shape in NamedShape.values()) {
                map[shape.code] = shape
            }
            map[46] = PointShapes.dot()
            SHAPE_BY_CODE = Collections.unmodifiableMap(map)
        }

        /**
         * @param value - integer [0..25] : codes of NamedShape
         * - '.' or code 46 : smallest rectangle (1px)
         * - A single character (or char code) : use this char as plotting symbol
         */
        private fun convert(value: Any?): PointShape {
            if (value == null) {
                return PointShapes.dot()
            }

            if (value is PointShape) {
                return value
            }
            return if (value is Number && SHAPE_BY_CODE.containsKey(value.toInt())) {
                SHAPE_BY_CODE[value.toInt()]!!
            } else charShape(value.toString())
        }

        private fun charShape(s: String): PointShape {
            if (!s.isEmpty()) {
                val ch = s[0]
                return if (ch == '.') {
                    PointShapes.dot()
                } else NamedShape.BULLET
                // TODO: implement
            }
            return PointShapes.dot()
        }
    }
}
