/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall

import org.jetbrains.letsPlot.core.plot.base.Aes

object Option {
    object Waterfall {
        const val X = "x"
        const val Y = "y"
        const val COLOR = "color"
        const val FILL = "fill"
        const val SIZE = "size"
        const val ALPHA = "alpha"
        const val LINE_TYPE = "linetype"
        const val WIDTH = "width"
        const val SHOW_LEGEND = "show_legend"
        const val TOOLTIPS = "tooltips"
        const val CALCULATE_TOTAL = "calc_total"
        const val TOTAL_TITLE = "total_title"
        const val SORTED_VALUE = "sorted_value"
        const val THRESHOLD = "threshold"
        const val MAX_VALUES = "max_values"
        const val H_LINE = "hline"
        const val H_LINE_ON_TOP = "hline_ontop"
        const val CONNECTOR = "connector"
    }

    object WaterfallBox {
        val AES_X = Aes.X
        val AES_YMIN = Aes.YMIN
        val AES_YMAX = Aes.YMAX
        val AES_COLOR = Aes.COLOR
        val AES_FILL = Aes.FILL

        object Var {
            const val X = "x"
            const val YMIN = "ymin"
            const val YMAX = "ymax"
            const val FLOW_TYPE = "flow_type"
            const val INITIAL = "initial"
            const val CUMULATIVE_SUM = "cumsum"
            const val DIFFERENCE = "dy"
        }
    }

    object WaterfallConnector {
        val AES_X = Aes.X
        val AES_Y = Aes.Y

        object Var {
            const val X = "x"
            const val Y = "y"
        }
    }
}