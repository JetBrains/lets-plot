/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall

object Option {
    object Waterfall {
        const val X = "x"
        const val Y = "y"
        const val COLOR = "color"
        const val FILL = "fill"
        const val CALCULATE_TOTAL = "calc_total"
        const val SORTED_VALUE = "sorted_value"
        const val THRESHOLD = "threshold"
        const val MAX_VALUES = "max_values"
    }

    object WaterfallBox {
        object Aes {
            const val X = "x"
            const val YMIN = "ymin"
            const val YMAX = "ymax"
            const val FILL = "fill"
        }
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
}