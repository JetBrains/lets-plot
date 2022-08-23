/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.coord

import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.base.spatial.projections.Projection
import jetbrains.datalore.base.spatial.projections.identity

internal abstract class CoordProviderBase(
    final override val xLim: DoubleSpan?,
    final override val yLim: DoubleSpan?,
    override val flipped: Boolean,
) : CoordProvider {

    override val projection: Projection = identity()

    init {
        require(xLim == null || xLim.length > 0.0) { "Coord x-limits range should be > 0.0" }
        require(yLim == null || yLim.length > 0.0) { "Coord y-limits range should be > 0.0" }
    }
}
