/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.core.rendering.primitives

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.vis.canvas.Canvas.Snapshot
import jetbrains.datalore.vis.canvas.Context2d

class Image : RenderBox {
    override val origin get() = position
    override val dimension get() = size
    var snapshot: Snapshot? = null
        set(value) {
            field = value
            isDirty = true
        }

    private var isDirty = true

    var position: DoubleVector = DoubleVector.ZERO
        set(value) {
            field = value
            isDirty = true
        }

    var size: DoubleVector = DoubleVector.ZERO
        set(value) {
            field = value
            isDirty = true
        }

    override fun render(ctx: Context2d) {
        isDirty = false
        snapshot?.let { ctx.drawImage(it, 0.0, 0.0, dimension.x, dimension.y) }
    }
}