/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.tooltip

class TooltipAnchor(
    val verticalAnchor: VerticalAnchor,
    val horizontalAnchor: HorizontalAnchor
) {
    enum class VerticalAnchor {
        TOP,
        BOTTOM,
        MIDDLE
    }

    enum class HorizontalAnchor {
        LEFT,
        RIGHT,
        CENTER;
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as TooltipAnchor

        if (verticalAnchor != other.verticalAnchor) return false
        if (horizontalAnchor != other.horizontalAnchor) return false

        return true
    }

    override fun hashCode(): Int {
        var result = verticalAnchor.hashCode()
        result = 31 * result + horizontalAnchor.hashCode()
        return result
    }
}