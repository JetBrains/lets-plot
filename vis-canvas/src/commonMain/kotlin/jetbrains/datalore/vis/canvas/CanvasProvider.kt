/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.canvas

import org.jetbrains.letsPlot.commons.intern.async.Async
import org.jetbrains.letsPlot.commons.geometry.Vector
import jetbrains.datalore.vis.canvas.Canvas.Snapshot

interface CanvasProvider {
    fun createCanvas(size: Vector): Canvas
    fun createSnapshot(dataUrl: String): Async<Snapshot>
    fun createSnapshot(bytes: ByteArray, size: Vector): Async<Snapshot>
}