/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.interact

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector

object InteractionUtil {
    fun viewportFromTransform(
        rect: DoubleRectangle,
        scale: DoubleVector = DoubleVector(1.0, 1.0),
        translate: DoubleVector = DoubleVector.ZERO
    ): DoubleRectangle {
        val origin = rect.origin.subtract(translate)
        val dimension = DoubleVector(
            rect.dimension.x * (1 / scale.x),
            rect.dimension.y * (1 / scale.y)
        )

        return DoubleRectangle(origin, dimension)
    }

    fun viewportFromScale(rect: DoubleRectangle, scale: Double, scaleOrigin: DoubleVector): DoubleRectangle {
        val newDim = rect.dimension.mul(scale)
        val newOrigin = rect.origin.add(scaleOrigin.subtract(rect.origin).mul(1 - scale))
        return DoubleRectangle(newOrigin, newDim)
    }

    fun viewportToTransform(rect: DoubleRectangle, viewport: DoubleRectangle): Pair<DoubleVector, DoubleVector> {
        val translate = rect.origin.subtract(viewport.origin)
        val scale = DoubleVector(
            rect.width / viewport.width,
            rect.height / viewport.height
        )

        return scale to translate
    }
}