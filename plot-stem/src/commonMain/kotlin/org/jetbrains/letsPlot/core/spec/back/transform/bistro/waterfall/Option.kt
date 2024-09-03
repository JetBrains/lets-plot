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
        const val BASE = "base"
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
                val X = DataFrame.Variable("..x..") // x position
                val XLAB = DataFrame.Variable("..xlabel..") // x label
                val YMIN = DataFrame.Variable("..ymin..") // min(initial, value)
                val YMIDDLE = DataFrame.Variable("..ymiddle..") // mean(ymin, ymax)
                val YMAX = DataFrame.Variable("..ymax..") // max(initial, value)
                val MEASURE = DataFrame.Variable("..measure..") // measure value: absolute/relative/total
                val FLOW_TYPE = DataFrame.Variable("..flow_type..") // flow type: increase/decrease/total
                val INITIAL = DataFrame.Variable("..initial..") // relative -> previous value || initial for whole group; absolute -> base
                val VALUE = DataFrame.Variable("..value..") // absolute -> original y; relative -> cumsum; total -> previous value || initial for whole group
                val DIFFERENCE = DataFrame.Variable("..dy..") // total -> difference between total cumsum and initial for whole group; else -> original y
                val RADIUS = DataFrame.Variable("..radius..") // (1 - box width) for all except last element in group; else -> 0
                val LABEL = DataFrame.Variable("..label..") // total -> ..value.. (= cumsum); else -> original y
            }
        }
    }
}