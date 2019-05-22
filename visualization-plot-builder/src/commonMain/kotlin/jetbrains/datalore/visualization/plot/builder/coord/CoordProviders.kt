package jetbrains.datalore.visualization.plot.builder.coord

import jetbrains.datalore.visualization.plot.builder.coord.map.MercatorProjectionY

object CoordProviders {
    fun cartesian(): CoordProvider {
        return CartesianCoordProvider()
    }

    fun fixed(ratio: Double): CoordProvider {
        return FixedRatioCoordProvider(ratio)
    }

    fun map(): CoordProvider {
        // Mercator projection is cylindrical thus we don't really need 'projection X'
        return ProjectionCoordProvider.withProjectionY(MercatorProjectionY())
    }
}
