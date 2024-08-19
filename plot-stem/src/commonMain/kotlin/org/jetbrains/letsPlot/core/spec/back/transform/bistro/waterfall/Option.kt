/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall

import org.jetbrains.letsPlot.core.plot.base.DataFrame

object Option {
    object Waterfall {
        const val NAME = "waterfall"
        const val X = "x"
        const val Y = "y"
        const val MEASURE = "measure"
        const val GROUP = "group"
        const val COLOR = "color"
        const val FILL = "fill"
        const val SIZE = "size"
        const val ALPHA = "alpha"
        const val LINE_TYPE = "linetype"
        const val WIDTH = "width"
        const val SHOW_LEGEND = "show_legend"
        const val RELATIVE_TOOLTIPS = "relative_tooltips"
        const val ABSOLUTE_TOOLTIPS = "absolute_tooltips"
        const val CALCULATE_TOTAL = "calc_total"
        const val TOTAL_TITLE = "total_title"
        const val SORTED_VALUE = "sorted_value"
        const val THRESHOLD = "threshold"
        const val MAX_VALUES = "max_values"
        const val H_LINE = "hline"
        const val H_LINE_ON_TOP = "hline_ontop"
        const val CONNECTOR = "connector"
        const val LABEL = "label"
        const val LABEL_FORMAT = "label_format"

        // Special values that parameters can take
        object Keyword {
            const val COLOR_FLOW_TYPE = "flow_type" // for params: color, fill, label#color
            const val TOOLTIP_DETAILED = "detailed" // for params: relative_tooltips, absolute_tooltips
        }

        object Var {
            val MEASURE_GROUP = DataFrame.Variable("..measure_group..")
            val DEF_MEASURE = DataFrame.Variable("..measure..")

            object Stat {
                val X = DataFrame.Variable("..x..")
                val XLAB = DataFrame.Variable("..xlabel..")
                val YMIN = DataFrame.Variable("..ymin..")
                val YMIDDLE = DataFrame.Variable("..ymiddle..")
                val YMAX = DataFrame.Variable("..ymax..")
                val MEASURE = DataFrame.Variable("..measure..")
                val FLOW_TYPE = DataFrame.Variable("..flow_type..")
                val INITIAL = DataFrame.Variable("..initial..")
                val VALUE = DataFrame.Variable("..value..")
                val DIFFERENCE = DataFrame.Variable("..dy..")
                val RADIUS = DataFrame.Variable("..radius..")
                val LABEL = DataFrame.Variable("..label..")
            }
        }
    }
}