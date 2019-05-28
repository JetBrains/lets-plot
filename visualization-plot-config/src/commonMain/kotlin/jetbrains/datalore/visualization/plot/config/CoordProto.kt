package jetbrains.datalore.visualization.plot.config

import jetbrains.datalore.visualization.plot.builder.coord.CoordProvider
import jetbrains.datalore.visualization.plot.builder.coord.CoordProviders

internal object CoordProto {
    // coordinate systems
    val CARTESIAN = "cartesian"
    private val FIXED = "fixed"
    private val MAP = "map"
    private val QUICK_MAP = "quickmap"  // todo
    private val FLIP = "flip"      // todo
    private val EQUAL = "equal"    // todo
    private val POLAR = "polar"    // todo
    private val TRANS = "trans"    // todo

    // option names
    private val X_LIM = "xlim"     // todo
    private val Y_LIM = "ylim"     // todo
    private val RATIO = "ratio"
    private val EXPAND = "expand"  // todo
    private val ORIENTATION = "orientation" // todo
    private val PROJECTION = "projection"   // todo

    fun createCoordProvider(coordName: String, options: OptionsAccessor): CoordProvider {
        when (coordName) {
            CARTESIAN -> return CoordProviders.cartesian()
            FIXED -> {
                var ratio = 1.0
                if (options.has(RATIO)) {
                    ratio = options.getDouble(RATIO)!!
                }
                return CoordProviders.fixed(ratio)
            }
            MAP -> return CoordProviders.map()
            else -> throw IllegalArgumentException("Unknown coordinate system name: '$coordName'")
        }
    }
}
