/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.guide

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
}