/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.plot.builder.coord.CoordProvider
import jetbrains.datalore.plot.builder.coord.CoordProviders
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
        val flipped = options.getBoolean(Option.Coord.FLIPPED)
        return when (coordName) {
            CARTESIAN -> CoordProviders.cartesian(xLim, yLim, flipped)
            FIXED -> CoordProviders.fixed(options.getDouble(RATIO) ?: 1.0, xLim, yLim, flipped)
            MAP -> {
                val projection = options.getString(Option.Coord.PROJECTION) ?: "mercator"
                CoordProviders.map(xLim, yLim, flipped, projection)
            }
            FLIP -> throw IllegalStateException("Don't try to instantiate coord FLIP, it's only a flag.")
            else -> throw IllegalArgumentException("Unknown coordinate system name: '$coordName'")
        }
    }
}
