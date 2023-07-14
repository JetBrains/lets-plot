/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.scale

enum class AxisPosition(
    private val id: String
) {
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

    val isLeft: Boolean
        get() = when (this) {
            LEFT, LR -> true
            else -> false
        }

    val isRight: Boolean
        get() = when (this) {
            RIGHT, LR -> true
            else -> false
        }

    val isTop: Boolean
        get() = when (this) {
            TOP, TB -> true
            else -> false
        }

    val isBottom: Boolean
        get() = when (this) {
            BOTTOM, TB -> true
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

    override fun toString(): String {
        return "AxisPosition $id"
    }
}
