/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.jetbrains.livemap.stubs

import org.jetbrains.letsPlot.commons.intern.async.Async
import org.jetbrains.letsPlot.commons.geometry.Vector
import jetbrains.datalore.vis.canvas.Canvas
import jetbrains.datalore.vis.canvas.Context2d
import org.mockito.Mockito

class CanvasStub: Canvas {
    override val context2d: Context2d = Mockito.mock(Context2d::class.java)
    override val size: Vector = Vector(600, 400)
    override fun takeSnapshot(): Async<Canvas.Snapshot> = TODO("Not yet implemented")
    override fun immidiateSnapshot(): Canvas.Snapshot = TODO("Not yet implemented")
}