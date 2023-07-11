/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.svg.dom.slim

import org.jetbrains.letsPlot.commons.values.Color

internal abstract class SlimBase protected constructor(val elementName: String) :
    SvgSlimShape {

    companion object {

        // attr indices
        internal const val fill = 0
        internal const val fillOpacity = 1
        internal const val stroke = 2
        internal const val strokeOpacity = 3
        internal const val strokeWidth = 4
        internal const val strokeTransform = 5
        internal const val classes = 6
        internal const val x1 = 7
        internal const val y1 = 8
        internal const val x2 = 9
        internal const val y2 = 10
        internal const val cx = 11
        internal const val cy = 12
        internal const val r = 13
        internal const val x = 14
        internal const val y = 15
        internal const val height = 16
        internal const val width = 17
        internal const val pathData = 18
        internal const val transform = 19

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
