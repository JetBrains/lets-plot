/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.commons.intern.spatial.projections.azimuthalEqualArea
import org.jetbrains.letsPlot.commons.intern.spatial.projections.conicEqualArea
import org.jetbrains.letsPlot.commons.intern.spatial.projections.identity
import org.jetbrains.letsPlot.commons.intern.spatial.projections.mercator
import org.jetbrains.letsPlot.core.plot.builder.coord.CoordProvider
import org.jetbrains.letsPlot.core.plot.builder.coord.CoordProviders
import org.jetbrains.letsPlot.core.spec.Option.Coord
import org.jetbrains.letsPlot.core.spec.Option.Coord.RATIO
import org.jetbrains.letsPlot.core.spec.Option.CoordName.CARTESIAN
import org.jetbrains.letsPlot.core.spec.Option.CoordName.FIXED
import org.jetbrains.letsPlot.core.spec.Option.CoordName.FLIP
import org.jetbrains.letsPlot.core.spec.Option.CoordName.MAP

internal object CoordProto {

    fun createCoordProvider(
        coordName: String,
        xLim: DoubleSpan?,
        yLim: DoubleSpan?,
        options: OptionsAccessor
    ): CoordProvider {
        val flipped = options.getBoolean(Coord.FLIPPED)
        return when (coordName) {
            CARTESIAN -> CoordProviders.cartesian(xLim, yLim, flipped)
            FIXED -> CoordProviders.fixed(options.getDouble(RATIO) ?: 1.0, xLim, yLim, flipped)
            MAP -> {
                val projection = when (options.getString(Coord.PROJECTION)) {
                    Coord.Projections.MERCATOR -> mercator()
                    Coord.Projections.IDENTITY -> identity()
                    "conic" -> conicEqualArea()
                    "azimuthal" -> azimuthalEqualArea()
                    else -> mercator()
                }

                CoordProviders.map(xLim, yLim, flipped, projection)
            }
            FLIP -> throw IllegalStateException("Don't try to instantiate coord FLIP, it's only a flag.")
            else -> throw IllegalArgumentException("Unknown coordinate system name: '$coordName'")
        }
    }
}
