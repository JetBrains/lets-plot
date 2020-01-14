/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.coord

import jetbrains.datalore.plot.builder.coord.map.MercatorProjectionY

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
