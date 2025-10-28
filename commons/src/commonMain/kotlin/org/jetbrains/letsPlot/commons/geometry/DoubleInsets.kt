/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.geometry

class DoubleInsets(
    val leftTop: DoubleVector,
    val rightBottom: DoubleVector,
) {
    constructor(
        left: Double,
        top: Double,
        right: Double,
        bottom: Double
    ) : this(
        leftTop = DoubleVector(left, top),
        rightBottom = DoubleVector(right, bottom),
    )

    val left: Double = leftTop.x
    val top: Double = leftTop.y
    val right: Double = rightBottom.x
    val bottom: Double = rightBottom.y


    fun subtractFrom(r: DoubleRectangle): DoubleRectangle {
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

    companion object {
        val ZERO = DoubleInsets(0.0, 0.0, 0.0, 0.0)
    }
}