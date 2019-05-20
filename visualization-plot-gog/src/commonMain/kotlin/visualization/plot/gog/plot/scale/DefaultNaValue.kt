package jetbrains.datalore.visualization.plot.gog.plot.scale

import jetbrains.datalore.base.typedKey.TypedKey
import jetbrains.datalore.base.typedKey.TypedKeyHashMap
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.plot.base.render.Aes
import jetbrains.datalore.visualization.plot.base.render.Aes.Companion.ALPHA
import jetbrains.datalore.visualization.plot.base.render.Aes.Companion.ANGLE
import jetbrains.datalore.visualization.plot.base.render.Aes.Companion.COLOR
import jetbrains.datalore.visualization.plot.base.render.Aes.Companion.FAMILY
import jetbrains.datalore.visualization.plot.base.render.Aes.Companion.FILL
import jetbrains.datalore.visualization.plot.base.render.Aes.Companion.FLOW
import jetbrains.datalore.visualization.plot.base.render.Aes.Companion.FONTFACE
import jetbrains.datalore.visualization.plot.base.render.Aes.Companion.FRAME
import jetbrains.datalore.visualization.plot.base.render.Aes.Companion.HEIGHT
import jetbrains.datalore.visualization.plot.base.render.Aes.Companion.HJUST
import jetbrains.datalore.visualization.plot.base.render.Aes.Companion.INTERCEPT
import jetbrains.datalore.visualization.plot.base.render.Aes.Companion.LABEL
import jetbrains.datalore.visualization.plot.base.render.Aes.Companion.LINETYPE
import jetbrains.datalore.visualization.plot.base.render.Aes.Companion.LOWER
import jetbrains.datalore.visualization.plot.base.render.Aes.Companion.MAP_ID
import jetbrains.datalore.visualization.plot.base.render.Aes.Companion.MIDDLE
import jetbrains.datalore.visualization.plot.base.render.Aes.Companion.SHAPE
import jetbrains.datalore.visualization.plot.base.render.Aes.Companion.SIZE
import jetbrains.datalore.visualization.plot.base.render.Aes.Companion.SLOPE
import jetbrains.datalore.visualization.plot.base.render.Aes.Companion.SPEED
import jetbrains.datalore.visualization.plot.base.render.Aes.Companion.UPPER
import jetbrains.datalore.visualization.plot.base.render.Aes.Companion.VJUST
import jetbrains.datalore.visualization.plot.base.render.Aes.Companion.WEIGHT
import jetbrains.datalore.visualization.plot.base.render.Aes.Companion.WIDTH
import jetbrains.datalore.visualization.plot.base.render.Aes.Companion.X
import jetbrains.datalore.visualization.plot.base.render.Aes.Companion.XEND
import jetbrains.datalore.visualization.plot.base.render.Aes.Companion.XINTERCEPT
import jetbrains.datalore.visualization.plot.base.render.Aes.Companion.XMAX
import jetbrains.datalore.visualization.plot.base.render.Aes.Companion.XMIN
import jetbrains.datalore.visualization.plot.base.render.Aes.Companion.Y
import jetbrains.datalore.visualization.plot.base.render.Aes.Companion.YEND
import jetbrains.datalore.visualization.plot.base.render.Aes.Companion.YINTERCEPT
import jetbrains.datalore.visualization.plot.base.render.Aes.Companion.YMAX
import jetbrains.datalore.visualization.plot.base.render.Aes.Companion.YMIN
import jetbrains.datalore.visualization.plot.base.render.Aes.Companion.Z
import jetbrains.datalore.visualization.plot.base.render.AestheticsUtil
import jetbrains.datalore.visualization.plot.base.render.linetype.NamedLineType
import jetbrains.datalore.visualization.plot.base.render.point.PointShapes

object DefaultNaValue {
    private val VALUE_MAP = TypedKeyHashMap()

    init {
        VALUE_MAP.put(X, 0.0)
        VALUE_MAP.put(Y, 0.0)
        VALUE_MAP.put(Z, 0.0)
        VALUE_MAP.put(YMIN, 0.0)
        VALUE_MAP.put(YMAX, 0.0)
        VALUE_MAP.put(COLOR, Color.GRAY)
        VALUE_MAP.put(FILL, Color.GRAY)
        VALUE_MAP.put(ALPHA, 0.0)
        VALUE_MAP.put(SHAPE, PointShapes.dot())
        VALUE_MAP.put(LINETYPE, NamedLineType.SOLID)
        VALUE_MAP.put(SIZE, AestheticsUtil.sizeFromCircleDiameter(1.0))
        VALUE_MAP.put(WIDTH, 1.0)
        VALUE_MAP.put(HEIGHT, 1.0)
        VALUE_MAP.put(WEIGHT, 1.0)
        VALUE_MAP.put(INTERCEPT, 0.0)
        VALUE_MAP.put(SLOPE, 1.0)
        VALUE_MAP.put(XINTERCEPT, 0.0)
        VALUE_MAP.put(YINTERCEPT, 0.0)
        VALUE_MAP.put(LOWER, 0.0)
        VALUE_MAP.put(MIDDLE, 0.0)
        VALUE_MAP.put(UPPER, 0.0)
        VALUE_MAP.put(MAP_ID, "empty map_id")
        VALUE_MAP.put(FRAME, "empty frame")
        VALUE_MAP.put(SPEED, 10.0)
        VALUE_MAP.put(FLOW, 0.1)
        VALUE_MAP.put(XMIN, 0.0)
        VALUE_MAP.put(XMAX, 0.0)
        VALUE_MAP.put(XEND, 0.0)
        VALUE_MAP.put(YEND, 0.0)
        VALUE_MAP.put(LABEL, "-")
        VALUE_MAP.put(FAMILY, "sans-serif")
        VALUE_MAP.put(FONTFACE, "plain")
        VALUE_MAP.put(HJUST, 0.5)  // 'middle'
        VALUE_MAP.put(VJUST, 0.5)  // 'middle'
        VALUE_MAP.put(ANGLE, 0.0)
    }

    /**
     * For test only (must br TRUE for any Aes)
     */
    fun has(aes: Aes<*>): Boolean {
        return VALUE_MAP.containsKey(aes)
    }

    operator fun <T> get(aes: Aes<T>): T {
        return VALUE_MAP[aes]
    }

    operator fun <T> get(aes: TypedKey<T>): T {
        return VALUE_MAP[aes]
    }
}
