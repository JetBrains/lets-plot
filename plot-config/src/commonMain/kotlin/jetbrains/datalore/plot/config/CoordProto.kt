/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.plot.builder.coord.CoordProvider
import jetbrains.datalore.plot.builder.coord.CoordProviders
import jetbrains.datalore.plot.config.Option.CoordName.CARTESIAN
import jetbrains.datalore.plot.config.Option.CoordName.FIXED
import jetbrains.datalore.plot.config.Option.CoordName.MAP

internal object CoordProto {

    // option names
    private const val X_LIM = "xlim"
    private const val Y_LIM = "ylim"
    private const val RATIO = "ratio"
    private const val EXPAND = "expand"  // todo
    private const val ORIENTATION = "orientation" // todo
    private const val PROJECTION = "projection"   // todo

    fun createCoordProvider(coordName: String, options: OptionsAccessor): CoordProvider {
        return when (coordName) {
            CARTESIAN -> CoordProviders.cartesian(
                options.getRangeOrNull(X_LIM),
                options.getRangeOrNull(Y_LIM)
            )
            FIXED -> CoordProviders.fixed(options.getDouble(RATIO) ?: 1.0)
            MAP -> CoordProviders.map()
            else -> throw IllegalArgumentException("Unknown coordinate system name: '$coordName'")
        }
    }
}
