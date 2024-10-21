/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.interact

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle

interface InteractionTarget {
    val geomBounds: DoubleRectangle

    val id: String?

    // Apply new viewport over the current one.
    // Passing the geomBounds will result in the same viewport.
    // Viewport change is additive:
    // applyViewport(geomBounds.add(10, 0))
    // applyViewport(geomBounds.add(10, 0))
    // will result a viewport that is shifted by 20 units to the right.
    // Returns a pair:
    //   - data bounds for the actual viewport.
    //   - coord flip flag.
    fun applyViewport(
        screenViewport: DoubleRectangle,
        ctx: InteractionContext
    ): Pair<DoubleRectangle, Boolean>
}
