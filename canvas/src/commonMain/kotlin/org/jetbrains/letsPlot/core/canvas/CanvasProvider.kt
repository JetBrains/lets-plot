/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.canvas

import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.intern.async.Async
import org.jetbrains.letsPlot.commons.values.Bitmap
import org.jetbrains.letsPlot.core.canvas.Canvas.Snapshot

interface CanvasProvider {
    fun createCanvas(size: Vector): Canvas
    fun createSnapshot(dataUrl: String): Async<Snapshot>
    fun createSnapshot(rgba: ByteArray, size: Vector): Async<Snapshot>
    fun immediateSnapshot(rgba: ByteArray, size: Vector): Snapshot
    fun immediateSnapshot(dataUrl: String): Snapshot
    fun snapshot(bitmap: Bitmap): Snapshot {
        return immediateSnapshot(bitmap.rgbaBytes(), Vector(bitmap.width, bitmap.height))
    }
}