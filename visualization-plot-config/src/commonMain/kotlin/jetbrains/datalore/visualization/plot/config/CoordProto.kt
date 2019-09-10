package jetbrains.datalore.visualization.plot.config

import jetbrains.datalore.visualization.plot.builder.coord.CoordProvider
import jetbrains.datalore.visualization.plot.builder.coord.CoordProviders
import jetbrains.datalore.visualization.plot.config.Option.CoordName.CARTESIAN
import jetbrains.datalore.visualization.plot.config.Option.CoordName.FIXED
import jetbrains.datalore.visualization.plot.config.Option.CoordName.MAP

internal object CoordProto {

    // option names
    private const val X_LIM = "xlim"     // todo
    private const val Y_LIM = "ylim"     // todo
    private const val RATIO = "ratio"
    private const val EXPAND = "expand"  // todo
    private const val ORIENTATION = "orientation" // todo
    private const val PROJECTION = "projection"   // todo

    fun createCoordProvider(coordName: String, options: OptionsAccessor): CoordProvider {
        return when (coordName) {
            CARTESIAN -> CoordProviders.cartesian()
            FIXED -> {
                var ratio = 1.0
                if (options.has(RATIO)) {
                    ratio = options.getDouble(RATIO)!!
                }
                CoordProviders.fixed(ratio)
            }
            MAP -> CoordProviders.map()
            else -> throw IllegalArgumentException("Unknown coordinate system name: '$coordName'")
        }
    }
}
