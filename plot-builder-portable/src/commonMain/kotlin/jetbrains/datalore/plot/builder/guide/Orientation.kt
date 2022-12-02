/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.guide

enum class Orientation(private val id: String) {
    LEFT("LEFT"),
    RIGHT("RIGHT"),
    TOP("TOP"),
    BOTTOM("BOTTOM");

    val isHorizontal: Boolean
        get() = this == TOP || this == BOTTOM

    fun flip(): Orientation {
        return when (this) {
            LEFT -> BOTTOM
            RIGHT -> TOP
            TOP -> RIGHT
            BOTTOM -> LEFT
        }
    }

    override fun toString(): String {
        return "Orientation $id"
    }
}
