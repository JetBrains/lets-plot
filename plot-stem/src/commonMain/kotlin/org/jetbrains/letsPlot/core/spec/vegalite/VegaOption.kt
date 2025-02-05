/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.vegalite

internal object VegaOption {
    object LetsPlotExt {
        const val LOG_LETS_PLOT_SPEC = "logLetsPlotSpec"
        const val REPORT_LETS_PLOT_CONVERTER_SUMMARY = "reportLetsPlotConverterSummary"
    }

    const val SCHEMA = "\$schema"
    const val DESCRIPTION = "description"
    const val DATA = "data"
    const val LAYER = "layer"
    const val MARK = "mark"
    const val FACET = "facet"

    const val CONCAT = "concat"
    const val VCONCAT = "vconcat"
    const val HCONCAT = "hconcat"
    const val REPEAT = "repeat"
    const val ENCODING = "encoding"
    const val TITLE = "title"

    const val CONFIG = "config"

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

        object TimeUnit {
            const val YEAR = "year"
            const val QUARTER = "quarter"
            const val MONTH = "month"
            const val DATE = "date"
            const val WEEK = "week"
            const val DAY = "day"
            const val HOURS = "hours"
            const val MINUTES = "minutes"
            const val SECONDS = "seconds"
            const val MILLISECONDS = "milliseconds"
        }

        object Aggregate {
            const val COUNT = "count"
            const val VALID = "valid"
            const val VALUES = "values"
            const val MISSING = "missing"
            const val DISTINCT = "distinct"
            const val SUM = "sum"
            const val PRODUCT = "product"
            const val MEAN = "mean"
            const val AVERAGE = "average"
            const val VARIANCE = "variance"
            const val VARIANCE_P = "variancep"
            const val STDEV = "stdev"
            const val STDEV_P = "stdevp"
            const val STDERR = "stderr"
            const val MEDIAN = "median"
            const val Q1 = "q1"
            const val Q3 = "q3"
            const val CI0 = "ci0"
            const val CI1 = "ci1"
            const val MIN = "min"
            const val MAX = "max"
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

        val Channels = setOf(
            Channel.X, Channel.Y, Channel.X2, Channel.Y2,
            Channel.LATITUDE, Channel.LONGITUDE,
            Channel.COLOR, Channel.FILL, Channel.OPACITY,
            Channel.STROKE, Channel.SIZE,
            Channel.ANGLE,
            Channel.SHAPE,
            Channel.TEXT,
            Channel.DETAIL,
            Channel.X_OFFSET, Channel.Y_OFFSET,
            Channel.ERROR_X
        )
        object Channel {
            const val X = "x"
            const val Y = "y"
            const val X2 = "x2"
            const val Y2 = "y2"

            const val LATITUDE = "latitude"
            const val LONGITUDE = "longitude"

            const val COLOR = "color"
            const val FILL = "fill"
            const val OPACITY = "opacity"
            const val STROKE = "stroke"
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

    object Title {
        const val TEXT = "text"
        const val SUBTITLE = "subtitle"
    }
}