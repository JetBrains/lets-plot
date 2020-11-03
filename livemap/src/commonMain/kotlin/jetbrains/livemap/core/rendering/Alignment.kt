/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.core.rendering

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.livemap.core.rendering.primitives.plus

class Alignment {
    var horizontal = HorizontalAlignment.RIGHT
    var vertical = VerticalAlignment.TOP

    enum class HorizontalAlignment {
        RIGHT,
        CENTER,
        LEFT
    }

    enum class VerticalAlignment {
        TOP,
        CENTER,
        BOTTOM
    }

    fun calculatePosition(origin: DoubleVector, dimension: DoubleVector): DoubleVector {
        val horizontalShift = when (horizontal) {
            HorizontalAlignment.LEFT -> -dimension.x
            HorizontalAlignment.CENTER -> -dimension.x / 2
            HorizontalAlignment.RIGHT -> 0.0
        }

        val verticalShift = when (vertical) {
            VerticalAlignment.TOP -> 0.0
            VerticalAlignment.CENTER -> -dimension.y / 2
            VerticalAlignment.BOTTOM -> -dimension.y
        }

        return origin + DoubleVector(horizontalShift, verticalShift)
    }
}