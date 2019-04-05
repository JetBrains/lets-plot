package jetbrains.datalore.visualization.base.svg.slim

import jetbrains.datalore.base.values.Color

internal abstract class SlimBase protected constructor(val elementName: String) : SvgSlimShape {

    fun setFill(c: Color, alpha: Double) {
        setAttribute(fill, c.toHexColor())
        if (alpha < 1.0) {
            setAttribute(fillOpacity, alpha.toString())
        }
    }

    fun setStroke(c: Color, alpha: Double) {
        setAttribute(stroke, c.toHexColor())
        if (alpha < 1.0) {
            setAttribute(strokeOpacity, alpha.toString())
        }
    }

    fun setStrokeWidth(v: Double) {
        setAttribute(strokeWidth, v.toString())
    }

    protected fun setAttribute(index: Int, v: Double) {
        setAttribute(index, v.toString())
    }

    protected abstract fun setAttribute(index: Int, v: Any)
    protected abstract fun hasAttribute(index: Int): Boolean
    protected abstract fun getAttribute(index: Int): Any

    companion object {

        // attr indices
        protected val fill = 0
        protected val fillOpacity = 1
        protected val stroke = 2
        protected val strokeOpacity = 3
        protected val strokeWidth = 4
        protected val strokeTransform = 5
        protected val classes = 6
        protected val x1 = 7
        protected val y1 = 8
        protected val x2 = 9
        protected val y2 = 10
        protected val cx = 11
        protected val cy = 12
        protected val r = 13
        protected val x = 14
        protected val y = 15
        protected val height = 16
        protected val width = 17
        protected val pathData = 18
        protected val transform = 19

        protected val ATTR_KEYS = arrayOf("fill", "fill-opacity", "stroke", "stroke-opacity", "stroke-width", "transform", "classes", "x1", "y1", "x2", "y2", "cx", "cy", "r", "x", "y", "height", "width", "d", "transform")

        protected val ATTR_COUNT = ATTR_KEYS.size
    }
}
