/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.canvas

import org.jetbrains.letsPlot.commons.geometry.Vector

abstract class ScaledCanvas protected constructor(
    context2d: Context2d,
    override val size: Vector,
    pixelRatio: Double
) : Canvas {
    final override val context2d: Context2d =
        if (pixelRatio == 1.0) context2d else ScaledContext2d(context2d, pixelRatio)
}
