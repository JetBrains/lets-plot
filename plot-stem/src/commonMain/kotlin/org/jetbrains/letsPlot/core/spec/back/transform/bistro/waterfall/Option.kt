/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall

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
    }

    object WaterfallBox {
        const val MEASURE_GROUP = "..measure_group.."
        const val DEF_MEASURE = "..measure.."

        object Var {
            const val X = "..x.."
            const val XLAB = "..xlabel.."
            const val YMIN = "..ymin.."
            const val YMAX = "..ymax.."
            const val MEASURE = "..measure.."
            const val FLOW_TYPE = "..flow_type.."
            const val INITIAL = "..initial.."
            const val VALUE = "..value.."
            const val DIFFERENCE = "..dy.."
        }
    }

    object WaterfallConnector {
        object Var {
            const val X = "..x.."
            const val Y = "..y.."
            const val RADIUS = "..radius.."
        }
    }

    object WaterfallLabel {
        object Var {
            const val X = "..x.."
            const val Y = "..y.."
            const val LABEL = "..label.."
            const val FLOW_TYPE = "..flow_type.."
        }
    }
}