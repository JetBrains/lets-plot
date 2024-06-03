/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.interact

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle

interface InteractionTarget {
    // Apply new viewport over the current one.
    // Passing the geomBounds will result in the same viewport.
    // Viewport change is additive:
    // applyViewport(geomBounds.add(10, 0))
    // applyViewport(geomBounds.add(10, 0))
    // will result a viewport that is shifted by 20 units to the right.
    // Returns data bounds for the actual viewport.
    fun applyViewport(screenViewport: DoubleRectangle): DoubleRectangle

    val geomBounds: DoubleRectangle
}
