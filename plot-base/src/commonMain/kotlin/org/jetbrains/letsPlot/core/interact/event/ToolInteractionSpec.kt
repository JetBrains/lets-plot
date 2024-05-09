/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.interact.event

object ToolInteractionSpec {
    const val WHEEL_ZOOM = "wheel-zoom"
    const val BOX_ZOOM = "box-zoom"
    const val DRAG_PAN = "drag-pan"

    // properties
    const val NAME = "name"
    const val ZOOM_DIRECTION = "direction"
    const val DRAG_DIRECTION = "direction"
}