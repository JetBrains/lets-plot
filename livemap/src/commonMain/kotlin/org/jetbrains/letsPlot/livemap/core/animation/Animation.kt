/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.core.animation

interface Animation {
    val isFinished: Boolean
    val duration: Double
    var time: Double
    fun animate()

    enum class Direction {
        FORWARD,
        BACK
    }

    enum class Loop {
        DISABLED,
        SWITCH_DIRECTION,
        KEEP_DIRECTION
    }
}
