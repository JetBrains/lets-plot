/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.interact.InteractionContext

abstract class ComponentTransientState(
    val viewBounds: DoubleRectangle
) {
    var scale: DoubleVector = DoubleVector(1.0, 1.0)
        private set
    var offset: DoubleVector = DoubleVector.ZERO
        private set

    abstract val dataBounds: DoubleRectangle
    abstract val isCoordFlip: Boolean

    fun applyDelta(
        scaleDelta: DoubleVector,
        offsetDelta: DoubleVector,
        ctx: InteractionContext
    ) {
        val offset = DoubleVector(
            offset.x + offsetDelta.x / scale.x,
            offset.y + offsetDelta.y / scale.y
        )
        val scale = DoubleVector(
            scale.x * scaleDelta.x,
            scale.y * scaleDelta.y
        )

        transformView(scale, offset, ctx)
    }

    fun reset() {
        // TODO
//        transformView(scale = DoubleVector(1.0, 1.0), offset = DoubleVector.ZERO)
    }

    fun transformView(scale: DoubleVector, offset: DoubleVector, ctx: InteractionContext) {
        this.scale = scale
        this.offset = offset

        syncDataBounds(ctx)
        repaint(ctx)
    }

    protected abstract fun syncDataBounds(ctx: InteractionContext)

    internal abstract fun repaint(ctx: InteractionContext)
}