/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout.util

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector

open class Insets(
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

    //    val leftTop = DoubleVector(left, top)
//    val rightBottom = DoubleVector(right, bottom)
    val left: Double = leftTop.x
    val top: Double = leftTop.y
    val right: Double = rightBottom.x
    val bottom: Double = rightBottom.y


    open fun subtractFrom(r: DoubleRectangle): DoubleRectangle {
//        val leftTop = DoubleVector(left, top)
//        val rightBottom = DoubleVector(right, bottom)

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
        val ZERO = Insets(0.0, 0.0, 0.0, 0.0)
    }
}