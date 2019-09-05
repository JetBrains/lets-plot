package jetbrains.datalore.visualization.plot.config

import jetbrains.datalore.visualization.plot.builder.coord.CoordProvider
import jetbrains.datalore.visualization.plot.builder.coord.CoordProviders

internal object CoordProto {
    // coordinate systems
    const val CARTESIAN = "cartesian"
    private const val FIXED = "fixed"
    private const val MAP = "map"
    private const val QUICK_MAP = "quickmap"  // todo
    private const val FLIP = "flip"      // todo
    private const val EQUAL = "equal"    // todo
    private const val POLAR = "polar"    // todo
    private const val TRANS = "trans"    // todo

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
