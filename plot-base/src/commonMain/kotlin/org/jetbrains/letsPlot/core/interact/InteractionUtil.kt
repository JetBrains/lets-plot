/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.interact

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector

object InteractionUtil {
    fun transformToViewport(
        rect: DoubleRectangle,
        scaleFactor: DoubleVector,
        translate: DoubleVector
    ): DoubleRectangle {
        val origin = rect.origin.add(translate)
        val dimension = DoubleVector(
            rect.dimension.x * (1 / scaleFactor.x),
            rect.dimension.y * (1 / scaleFactor.y)
        )

        return DoubleRectangle(origin, dimension)
    }

    fun viewportToTransform(viewport: DoubleRectangle, rect: DoubleRectangle): Pair<DoubleVector, DoubleVector> {
        val translate = rect.origin.subtract(viewport.origin)
        val scale = DoubleVector(
            rect.width / viewport.width,
            rect.height / viewport.height
        )

        return scale to translate
    }

    fun scaleViewport(rect: DoubleRectangle, scaleFactor: Double, scaleOrigin: DoubleVector): DoubleRectangle {
        val newDim = rect.dimension.mul(scaleFactor)
        val newOrigin = rect.origin.add(scaleOrigin.subtract(rect.origin).mul(1 - scaleFactor))
        return DoubleRectangle(newOrigin, newDim)
    }
}