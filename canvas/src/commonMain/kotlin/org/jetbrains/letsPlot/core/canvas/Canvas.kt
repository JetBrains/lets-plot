/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.canvas

import org.jetbrains.letsPlot.commons.intern.async.Async
import org.jetbrains.letsPlot.commons.geometry.Vector

interface Canvas {
    val context2d: Context2d

    val size: Vector

    fun takeSnapshot(): Async<Snapshot>
    fun immidiateSnapshot(): Snapshot

    interface Snapshot {
        fun copy(): Snapshot
    }
}
