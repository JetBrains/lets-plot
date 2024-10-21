/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.vegalite

internal object VegaOption {
    const val DATA = "data"
    const val LAYER = "layer"
    const val MARK = "mark"
    const val FACET = "facet"
    const val REPEAT = "repeat"
    const val ENCODING = "encoding"

    object Data {
        const val VALUES = "values"
        const val URL = "url"
    }

    object Encoding {
        const val FIELD = "field"
        const val VALUE = "value"
        const val TYPE = "type"
        const val BIN = "bin"
        const val TIMEUNIT = "timeUnit"
        const val AGGREGATE = "aggregate"
        const val BAND = "band"
        const val TITLE = "title"

        const val SCALE = "scale"
        const val AXIS = "axis"
        const val LEGEND = "legend"
        const val FORMAT = "format"
        const val STACK = "stack"
        const val SORT = "sort"
        const val CONDITION = "condition"
        const val TOOLTIP = "tooltip"
        const val FORMAT_TYPE = "formatType"

        object Aggregate {
            const val COUNT = "count"
            const val SUM = "sum"
            const val MEAN = "mean"
            const val ARGMIN = "argmin"
            const val ARGMAX = "argmax"
        }

        object Scale {
            const val DOMAIN = "domain"
            const val DOMAIN_MIN = "domainMin"
            const val DOMAIN_MAX = "domainMax"
        }

        object Stack {
            const val ZERO = "zero"
            const val NORMALIZE = "normalize"
            const val CENTER = "center"
        }

        object Channel {
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
            const val DETAIL = "detail"

            const val X_OFFSET = "xOffset"
            const val Y_OFFSET = "yOffset"

            const val ERROR_X = "errorX"
        }

        object Types {
            const val QUANTITATIVE = "quantitative"
            const val ORDINAL = "ordinal"
            const val NOMINAL = "nominal"
            const val TEMPORAL = "temporal"
        }
    }

    object Mark {
        const val TYPE = "type"
        const val TRANSFORM = "transform"
        const val WIDTH = "width"
        object Width {
            const val BAND = "band"
        }

        object Types {
            const val BAR = "bar"
            const val LINE = "line"
            const val TRAIL = "trail"
            const val POINT = "point"
            const val AREA = "area"
            const val BOXPLOT = "boxplot"
            const val TEXT = "text"
            const val RECT = "rect"
            const val RULE = "rule"
            const val CIRCLE = "circle"
            const val SQUARE = "square"
            const val ERROR_BAR = "errorbar"
            const val ERROR_BAND = "errorband"
            const val TICK = "tick"
        }
    }

    object Transform {
        const val DENSITY = "density"

        object Bin {
            const val MAXBINS = "maxbins"
            const val STEP = "step"
        }

        object Density {
            const val DENSITY = "density"
            const val GROUP_BY = "groupby"
            const val AS = "as"

            const val VAR_VALUE = "value"
            const val VAR_DENSITY = "density"
        }
    }

}