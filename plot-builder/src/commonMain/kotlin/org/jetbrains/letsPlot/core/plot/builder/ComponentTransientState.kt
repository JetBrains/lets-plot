/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTransformBuilder

abstract class ComponentTransientState(
    val viewBounds: DoubleRectangle
) {
    var scale: DoubleVector = DoubleVector(1.0, 1.0)
        private set
    var offset: DoubleVector = DoubleVector.ZERO
        private set
    var transform = SvgTransformBuilder().build()
        private set

    abstract val dataBounds: DoubleRectangle

    fun scale(scale: DoubleVector) = transformView(scale, this.offset)

    fun translate(offset: DoubleVector) = transformView(this.scale, offset)

    fun applyDelta(scaleDelta: DoubleVector, offsetDelta: DoubleVector) {
        val offset = DoubleVector(
            offset.x + offsetDelta.x / scale.x,
            offset.y + offsetDelta.y / scale.y
        )
        val scale = DoubleVector(
            scale.x * scaleDelta.x,
            scale.y * scaleDelta.y
        )

        transformView(scale, offset)
    }

    fun reset() = transformView(scale = DoubleVector(1.0, 1.0), offset = DoubleVector.ZERO)

    open fun transformView(scale: DoubleVector, offset: DoubleVector) {
        this.scale = scale
        this.offset = offset

        this.transform = SvgTransformBuilder()
            .scale(scale.x, scale.y)
            .translate(offset)
            .build()

        repaint()
    }

    internal abstract fun repaint()
}