/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.canvas

import org.jetbrains.letsPlot.commons.geometry.Vector

// TODO: CANVAS_REFACTOR: implement CanvasProvider
interface Canvas {
    val context2d: Context2d
    val size: Vector

    fun takeSnapshot(): Snapshot

    interface Snapshot {
        val size: Vector
        fun copy(): Snapshot
    }
}
