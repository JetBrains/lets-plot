/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.canvas

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.geometry.Vector

interface Canvas {
    val context2d: Context2d

    val size: Vector

    fun takeSnapshot(): Async<Snapshot>
    fun immidiateSnapshot(): Snapshot

    interface Snapshot {
        fun copy(): Snapshot
    }
}
