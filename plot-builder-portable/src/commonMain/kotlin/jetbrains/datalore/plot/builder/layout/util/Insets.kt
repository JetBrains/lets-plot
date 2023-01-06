/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout.util

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector

internal open class Insets(
    val left: Double,
    val top: Double,
    val right: Double,
    val bottom: Double
) {

    val leftTop = DoubleVector(left, top)
    val rightBottom = DoubleVector(right, bottom)

    open fun subtractFrom(r: DoubleRectangle): DoubleRectangle {
        val leftTop = DoubleVector(left, top)
        val rightBottom = DoubleVector(right, bottom)

        // ? can be negative
        val size = r.dimension
            .subtract(leftTop)
            .subtract(rightBottom)

        return DoubleRectangle(
            r.origin.add(leftTop),
            size
        )
    }

    override fun toString(): String {
        return "Insets(left=$left, top=$top, right=$right, bottom=$bottom)"
    }
}