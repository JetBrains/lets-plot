/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.vegalite

internal object Option {
    const val DATA = "data"
    const val VALUES = "values"
    const val LAYER = "layer"
    const val MARK = "mark"
    const val FACET = "facet"
    const val REPEAT = "repeat"

    object Encodings {
        const val ENCODING = "encoding"
        const val FIELD = "field"
        const val VALUE = "value"
        const val TYPE = "type"

        object Channels {
            const val X = "x"
            const val Y = "y"
            const val X2 = "x2"
            const val Y2 = "y2"

            const val COLOR = "color"
            const val FILL = "fill"
            const val OPACITY = "opacity"
            const val FILL_OPACITY = "fillOpacity"
            const val STROKE = "stroke"
            const val STROKE_OPACITY = "strokeOpacity"
            const val STROKE_WIDTH = "strokeWidth"
            const val STROKE_DASH = "strokeDash"
            const val SIZE = "size"
            const val ANGLE = "angle"
            const val SHAPE = "shape"
            const val TEXT = "text"

        }

        object Type {
            const val QUANTITATIVE = "quantitative"
            const val ORDINAL = "ordinal"
            const val NOMINAL = "nominal"
            const val TEMPORAL = "temporal"
        }
    }

    object Marks {
        const val BAR = "bar"
        const val LINE = "line"
        const val POINT = "point"
    }
}