/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.tooltip

enum class VerticalAlignment {
    TOP,
    BOTTOM,
    MIDDLE
}

enum class HorizontalAlignment {
    LEFT,
    RIGHT,
    CENTER;

    fun inversed(): HorizontalAlignment {
        return when (this) {
            LEFT -> RIGHT
            RIGHT -> LEFT
            CENTER -> CENTER
        }
    }
}