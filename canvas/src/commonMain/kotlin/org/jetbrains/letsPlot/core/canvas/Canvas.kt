/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.canvas

import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.values.Bitmap

// TODO: CANVAS_REFACTOR: implement CanvasProvider
interface Canvas {
    val context2d: Context2d
    val size: Vector // TODO: should be in pixels, not in points

    fun takeSnapshot(): Snapshot

    interface Snapshot {
        val size: Vector
        val bitmap: Bitmap
        fun copy(): Snapshot

        fun dispose()
    }
}
