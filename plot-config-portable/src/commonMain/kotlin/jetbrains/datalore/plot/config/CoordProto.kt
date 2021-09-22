/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.plot.builder.coord.CoordProvider
import jetbrains.datalore.plot.builder.coord.CoordProviders
import jetbrains.datalore.plot.config.Option.CoordName.CARTESIAN
import jetbrains.datalore.plot.config.Option.CoordName.FIXED
import jetbrains.datalore.plot.config.Option.CoordName.FLIP
import jetbrains.datalore.plot.config.Option.CoordName.MAP

internal object CoordProto {

    // option names
    private const val X_LIM = "xlim"
    private const val Y_LIM = "ylim"
    private const val RATIO = "ratio"

    //    private const val FLIP = "flip"
    private const val EXPAND = "expand"  // todo
    private const val ORIENTATION = "orientation" // Todo: see 'coord_map'
    private const val PROJECTION = "projection"   // todo

    fun createCoordProvider(coordName: String, options: OptionsAccessor): CoordProvider {
        val xLim = options.getRangeOrNull(X_LIM)
        val yLim = options.getRangeOrNull(Y_LIM)
        val flipped = options.getBoolean(Option.Coord.FLIPPED)
        return when (coordName) {
            CARTESIAN -> CoordProviders.cartesian(xLim, yLim, flipped)
            FIXED -> CoordProviders.fixed(options.getDouble(RATIO) ?: 1.0, xLim, yLim, flipped)
            MAP -> CoordProviders.map(xLim, yLim, flipped)
            FLIP -> throw IllegalStateException("Don't try to instantiate coord FLIP, it's only a flag.")
            else -> throw IllegalArgumentException("Unknown coordinate system name: '$coordName'")
        }
    }

    fun createCoordProvider(defaultProvider: CoordProvider, options: OptionsAccessor): CoordProvider {
        val xLim = options.getRangeOrNull(X_LIM)
        val yLim = options.getRangeOrNull(Y_LIM)
        val flipped = options.getBoolean(Option.Coord.FLIPPED)
        return defaultProvider.with(xLim, yLim, flipped)
    }
}
