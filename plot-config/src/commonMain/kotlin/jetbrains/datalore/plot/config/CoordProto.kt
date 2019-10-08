package jetbrains.datalore.plot.config

import jetbrains.datalore.plot.config.Option.CoordName.CARTESIAN
import jetbrains.datalore.plot.config.Option.CoordName.FIXED
import jetbrains.datalore.plot.config.Option.CoordName.MAP

internal object CoordProto {

    // option names
    private const val X_LIM = "xlim"     // todo
    private const val Y_LIM = "ylim"     // todo
    private const val RATIO = "ratio"
    private const val EXPAND = "expand"  // todo
    private const val ORIENTATION = "orientation" // todo
    private const val PROJECTION = "projection"   // todo

    fun createCoordProvider(coordName: String, options: OptionsAccessor): jetbrains.datalore.plot.builder.coord.CoordProvider {
        return when (coordName) {
            CARTESIAN -> jetbrains.datalore.plot.builder.coord.CoordProviders.cartesian()
            FIXED -> {
                var ratio = 1.0
                if (options.has(RATIO)) {
                    ratio = options.getDouble(RATIO)!!
                }
                jetbrains.datalore.plot.builder.coord.CoordProviders.fixed(ratio)
            }
            MAP -> jetbrains.datalore.plot.builder.coord.CoordProviders.map()
            else -> throw IllegalArgumentException("Unknown coordinate system name: '$coordName'")
        }
    }
}
