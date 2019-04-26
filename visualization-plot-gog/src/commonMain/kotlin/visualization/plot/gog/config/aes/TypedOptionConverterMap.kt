package jetbrains.datalore.visualization.plot.gog.config.aes

import jetbrains.datalore.base.function.Function
import jetbrains.datalore.base.function.Functions.function
import jetbrains.datalore.visualization.plot.gog.core.render.Aes
import jetbrains.datalore.visualization.plot.gog.core.render.Aes.Companion.ALPHA
import jetbrains.datalore.visualization.plot.gog.core.render.Aes.Companion.ANGLE
import jetbrains.datalore.visualization.plot.gog.core.render.Aes.Companion.COLOR
import jetbrains.datalore.visualization.plot.gog.core.render.Aes.Companion.FAMILY
import jetbrains.datalore.visualization.plot.gog.core.render.Aes.Companion.FILL
import jetbrains.datalore.visualization.plot.gog.core.render.Aes.Companion.FLOW
import jetbrains.datalore.visualization.plot.gog.core.render.Aes.Companion.FONTFACE
import jetbrains.datalore.visualization.plot.gog.core.render.Aes.Companion.FRAME
import jetbrains.datalore.visualization.plot.gog.core.render.Aes.Companion.HEIGHT
import jetbrains.datalore.visualization.plot.gog.core.render.Aes.Companion.HJUST
import jetbrains.datalore.visualization.plot.gog.core.render.Aes.Companion.INTERCEPT
import jetbrains.datalore.visualization.plot.gog.core.render.Aes.Companion.LABEL
import jetbrains.datalore.visualization.plot.gog.core.render.Aes.Companion.LINETYPE
import jetbrains.datalore.visualization.plot.gog.core.render.Aes.Companion.LOWER
import jetbrains.datalore.visualization.plot.gog.core.render.Aes.Companion.MAP_ID
import jetbrains.datalore.visualization.plot.gog.core.render.Aes.Companion.MIDDLE
import jetbrains.datalore.visualization.plot.gog.core.render.Aes.Companion.SHAPE
import jetbrains.datalore.visualization.plot.gog.core.render.Aes.Companion.SIZE
import jetbrains.datalore.visualization.plot.gog.core.render.Aes.Companion.SLOPE
import jetbrains.datalore.visualization.plot.gog.core.render.Aes.Companion.SPEED
import jetbrains.datalore.visualization.plot.gog.core.render.Aes.Companion.UPPER
import jetbrains.datalore.visualization.plot.gog.core.render.Aes.Companion.VJUST
import jetbrains.datalore.visualization.plot.gog.core.render.Aes.Companion.WEIGHT
import jetbrains.datalore.visualization.plot.gog.core.render.Aes.Companion.WIDTH
import jetbrains.datalore.visualization.plot.gog.core.render.Aes.Companion.X
import jetbrains.datalore.visualization.plot.gog.core.render.Aes.Companion.XEND
import jetbrains.datalore.visualization.plot.gog.core.render.Aes.Companion.XINTERCEPT
import jetbrains.datalore.visualization.plot.gog.core.render.Aes.Companion.XMAX
import jetbrains.datalore.visualization.plot.gog.core.render.Aes.Companion.XMIN
import jetbrains.datalore.visualization.plot.gog.core.render.Aes.Companion.Y
import jetbrains.datalore.visualization.plot.gog.core.render.Aes.Companion.YEND
import jetbrains.datalore.visualization.plot.gog.core.render.Aes.Companion.YINTERCEPT
import jetbrains.datalore.visualization.plot.gog.core.render.Aes.Companion.YMAX
import jetbrains.datalore.visualization.plot.gog.core.render.Aes.Companion.YMIN
import jetbrains.datalore.visualization.plot.gog.core.render.Aes.Companion.Z

internal class TypedOptionConverterMap {

    private val myMap = HashMap<Aes<*>, Function<Any?, *>>()

    init {
        this.put(X, DOUBLE_CVT)
        this.put(Y, DOUBLE_CVT)

        this.put(Z, DOUBLE_CVT)
        this.put(YMIN, DOUBLE_CVT)
        this.put(YMAX, DOUBLE_CVT)
        this.put(COLOR, COLOR_CVT)
        this.put(FILL, COLOR_CVT)
        this.put(ALPHA, DOUBLE_CVT)
        this.put(SHAPE, SHAPE_CVT)
        this.put(LINETYPE, LINETYPE_CVT)

        this.put(SIZE, DOUBLE_CVT)
        this.put(WIDTH, DOUBLE_CVT)
        this.put(HEIGHT, DOUBLE_CVT)
        this.put(WEIGHT, DOUBLE_CVT)
        this.put(INTERCEPT, DOUBLE_CVT)
        this.put(SLOPE, DOUBLE_CVT)
        this.put(XINTERCEPT, DOUBLE_CVT)
        this.put(YINTERCEPT, DOUBLE_CVT)
        this.put(LOWER, DOUBLE_CVT)
        this.put(MIDDLE, DOUBLE_CVT)
        this.put(UPPER, DOUBLE_CVT)

        this.put(MAP_ID, IDENTITY_O_CVT)
        this.put(FRAME, IDENTITY_S_CVT)

        this.put(SPEED, DOUBLE_CVT)
        this.put(FLOW, DOUBLE_CVT)

        this.put(XMIN, DOUBLE_CVT)
        this.put(XMAX, DOUBLE_CVT)
        this.put(XEND, DOUBLE_CVT)
        this.put(YEND, DOUBLE_CVT)

        this.put(LABEL, IDENTITY_S_CVT)
        this.put(FAMILY, IDENTITY_S_CVT)
        this.put(FONTFACE, IDENTITY_S_CVT)
        this.put(HJUST, IDENTITY_O_CVT)   // text horizontal justification (numbers [0..1] or predefined strings, DOUBLE_CVT; not positional)
        this.put(VJUST, IDENTITY_O_CVT)   // text vertical justification (numbers [0..1] or predefined strings, not positional)
        this.put(ANGLE, DOUBLE_CVT)
    }

    operator fun <T> get(aes: Aes<T>): Function<Any, T> {
        // Safe cast if 'put' is used responsibly.
        return myMap[aes] as Function<Any, T>
    }

    private fun <T> put(aes: Aes<T>, value: Function<Any?, T?>): Function<Any?, T?> {
        // Used responsibly, private access
        return myMap.put(aes, value) as Function<Any?, T?>
    }

    fun containsKey(aes: Aes<*>): Boolean {
        return myMap.containsKey(aes)
    }

    companion object {
        private val IDENTITY_O_CVT = function { o: Any? -> o }
        private val IDENTITY_S_CVT = function { o: Any? -> if (o == null) null else o.toString() }
        private val DOUBLE_CVT = NumericOptionConverter()
        private val COLOR_CVT = ColorOptionConverter()
        private val SHAPE_CVT = ShapeOptionConverter()
        private val LINETYPE_CVT = LineTypeOptionConverter()
    }
}
