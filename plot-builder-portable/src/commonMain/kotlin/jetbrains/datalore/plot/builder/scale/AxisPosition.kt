/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.scale

enum class AxisPosition(private val id: String) {
    LEFT("LEFT"),
    RIGHT("RIGHT"),
    TOP("TOP"),
    BOTTOM("BOTTOM"),
    LR("LR"),
    TB("TB");

    val isHorizontal: Boolean
        get() = when (this) {
            TOP, BOTTOM, TB -> true
            else -> false
        }

    val isVertical: Boolean
        get() = when (this) {
            LEFT, RIGHT, LR -> true
            else -> false
        }

    val isDoubleSided: Boolean
        get() = when (this) {
            LR, TB -> true
            else -> false
        }

    fun flip(): AxisPosition {
        return when (this) {
            // vert -> horiz
            LEFT -> BOTTOM
            RIGHT -> TOP
            LR -> TB

            // horiz -> vert
            TOP -> RIGHT
            BOTTOM -> LEFT
            TB -> LR
        }
    }

    fun split(): Pair<AxisPosition, AxisPosition> {
        check(isDoubleSided)
        return when (this) {
            LR -> LEFT to RIGHT
            else -> TOP to BOTTOM
        }
    }

    override fun toString(): String {
        return "AxisPosition $id"
    }
}
