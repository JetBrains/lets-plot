/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.tooltip

enum class HorizontalAxisTooltipPosition {
    TOP,
    BOTTOM;

    val isTop: Boolean
        get() = this == TOP

    val isBottom: Boolean
        get() = this == BOTTOM
}

enum class VerticalAxisTooltipPosition {
    LEFT,
    RIGHT;

    val isLeft: Boolean
        get() = this == LEFT

    val isRight: Boolean
        get() = this == RIGHT
}