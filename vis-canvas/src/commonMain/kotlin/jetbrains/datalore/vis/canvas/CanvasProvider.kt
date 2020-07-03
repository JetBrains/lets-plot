/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.canvas

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.vis.canvas.Canvas.Snapshot

interface CanvasProvider {
    fun createCanvas(size: Vector): Canvas
    fun createSnapshot(dataUrl: String): Async<Snapshot>
    fun createSnapshot(bytes: ByteArray, size: Vector): Async<Snapshot>
}