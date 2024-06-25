/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.interact.InteractionUtil
import org.jetbrains.letsPlot.core.plot.base.render.svg.GroupComponent
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTransformBuilder

abstract class ComponentTransientState(
    val bounds: DoubleRectangle
) {
    var scale: DoubleVector = DoubleVector(1.0, 1.0)
        private set
    var offset: DoubleVector = DoubleVector.ZERO
        private set
    var transform = SvgTransformBuilder().build()
        private set
    lateinit var dataBounds: DoubleRectangle
        private set

    fun scale(scale: DoubleVector) = transform(scale, this.offset)

    fun translate(offset: DoubleVector) = transform(this.scale, offset)

    fun applyDelta(scaleDelta: DoubleVector, offsetDelta: DoubleVector) {
        val offset = DoubleVector(
            offset.x + offsetDelta.x / scale.x,
            offset.y + offsetDelta.y / scale.y
        )
        val scale = DoubleVector(
            scale.x * scaleDelta.x,
            scale.y * scaleDelta.y
        )

        transform(scale, offset)
    }

    fun reset() = transform(scale = DoubleVector(1.0, 1.0), offset = DoubleVector.ZERO)

    fun transform(scale: DoubleVector, offset: DoubleVector) {
        this.scale = scale
        this.offset = offset

        this.transform = SvgTransformBuilder()
            .scale(scale.x, scale.y)
            .translate(offset)
            .build()

        this.dataBounds = calculateDataBounds()

        repaint()
    }

    private fun calculateDataBounds(): DoubleRectangle {
        val viewport = InteractionUtil.viewportFromTransform(
            rect = bounds,
            scale = scale,
            translate = offset
        )
        return toDataBounds(viewport.subtract(bounds.origin))
    }

    abstract fun toDataBounds(clientRect: DoubleRectangle): DoubleRectangle

    internal abstract fun repaint()

    internal abstract fun repaintFrame(bottomGroup: GroupComponent, topGroup: GroupComponent)
}