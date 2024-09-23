/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.interact.event

object ToolInteractionSpec {
    // key

    const val NAME = "name"
    const val ZOOM_DIRECTION = "direction"
    const val DRAG_DIRECTION = "direction"
    const val ZOOM_BOX_MODE = "zoom-box-mode"

    // value

    // name
    const val WHEEL_ZOOM = "wheel-zoom"
    const val BOX_ZOOM = "box-zoom"
    const val DRAG_PAN = "drag-pan"
    const val ROLLBACK_ALL_CHANGES = "rollback-all-changes"

    object ZoomBoxMode {
        const val CORNER_START = "corner_start"
        const val CENTER_START = "center_start"
    }
}