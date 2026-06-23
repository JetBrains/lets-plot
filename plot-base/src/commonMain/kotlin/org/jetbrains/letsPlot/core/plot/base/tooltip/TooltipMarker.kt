/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.tooltip

import org.jetbrains.letsPlot.commons.values.Color

class TooltipMarker(
    val majorColor: Color? = null,
    val minorColor: Color? = null
) {

    fun allTransparent(): Boolean {
        return (majorColor == null || majorColor.alpha == 0) &&
                (minorColor == null || minorColor.alpha == 0)
    }

    companion object {
        val NONE = TooltipMarker()

        fun create(majorColor: Color?, minorColor: Color? = null): TooltipMarker {
            return if (majorColor == null && minorColor == null) {
                NONE
            } else {
                TooltipMarker(majorColor, minorColor)
            }
        }
    }
}
