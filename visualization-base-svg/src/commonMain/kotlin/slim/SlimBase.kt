package jetbrains.datalore.visualization.base.svg.slim

import jetbrains.datalore.base.values.Color

internal abstract class SlimBase protected constructor(val elementName: String) : SvgSlimShape {

    companion object {

        // attr indices
        internal val fill = 0
        internal val fillOpacity = 1
        internal val stroke = 2
        internal val strokeOpacity = 3
        internal val strokeWidth = 4
        internal val strokeTransform = 5
        internal val classes = 6
        internal val x1 = 7
        internal val y1 = 8
        internal val x2 = 9
        internal val y2 = 10
        internal val cx = 11
        internal val cy = 12
        internal val r = 13
        internal val x = 14
        internal val y = 15
        internal val height = 16
        internal val width = 17
        internal val pathData = 18
        internal val transform = 19

        internal val ATTR_KEYS = arrayOf("fill", "fill-opacity", "stroke", "stroke-opacity", "stroke-width", "transform", "classes", "x1", "y1", "x2", "y2", "cx", "cy", "r", "x", "y", "height", "width", "d", "transform")
        internal val ATTR_COUNT = ATTR_KEYS.size
    }

    override fun setFill(c: Color, alpha: Double) {
        setAttribute(fill, c.toHexColor())
        if (alpha < 1.0) {
            setAttribute(fillOpacity, alpha.toString())
        }
    }

    override fun setStroke(c: Color, alpha: Double) {
        setAttribute(stroke, c.toHexColor())
        if (alpha < 1.0) {
            setAttribute(strokeOpacity, alpha.toString())
        }
    }

    override fun setStrokeWidth(v: Double) {
        setAttribute(strokeWidth, v.toString())
    }

    internal fun setAttribute(index: Int, v: Double) {
        setAttribute(index, v.toString())
    }

    internal abstract fun setAttribute(index: Int, v: Any)
    internal abstract fun hasAttribute(index: Int): Boolean
    internal abstract fun getAttribute(index: Int): Any?
}
