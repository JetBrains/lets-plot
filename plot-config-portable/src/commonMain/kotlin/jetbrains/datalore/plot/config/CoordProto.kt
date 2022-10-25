/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.base.spatial.projections.azimuthalEqualArea
import jetbrains.datalore.base.spatial.projections.conicEqualArea
import jetbrains.datalore.base.spatial.projections.identity
import jetbrains.datalore.base.spatial.projections.mercator
import jetbrains.datalore.plot.builder.coord.CoordProvider
import jetbrains.datalore.plot.builder.coord.CoordProviders
import jetbrains.datalore.plot.config.Option.Coord
import jetbrains.datalore.plot.config.Option.Coord.RATIO
import jetbrains.datalore.plot.config.Option.CoordName.CARTESIAN
import jetbrains.datalore.plot.config.Option.CoordName.FIXED
import jetbrains.datalore.plot.config.Option.CoordName.FLIP
import jetbrains.datalore.plot.config.Option.CoordName.MAP

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
