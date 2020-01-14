/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.base.gcommon.collect.ClosedRange
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
            CARTESIAN -> {

                // TODO: add `getRangeOrNull()` to OptionsAccessor (we already have `getRange()` there)
                fun toRange(pair: List<Double>): ClosedRange<Double>? = when {
                    pair.size == 2 -> ClosedRange.closed(pair.first(), pair.last())
                    else -> null
                }

                val xLim = toRange(options.getDoubleList(X_LIM))
                val yLim = toRange(options.getDoubleList(Y_LIM))
                CoordProviders.cartesian(xLim, yLim)
            }
            FIXED -> CoordProviders.fixed(options.getDouble(RATIO) ?: 1.0)
            MAP -> CoordProviders.map()
            else -> throw IllegalArgumentException("Unknown coordinate system name: '$coordName'")
        }
    }
}
