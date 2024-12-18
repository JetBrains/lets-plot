/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.livemap.common.component

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.spatial.GeoRectangle
import org.jetbrains.letsPlot.livemap.api.LiveMapBuilder
import org.jetbrains.letsPlot.livemap.api.MapLocation

class VectorTilesPerfDemoModel(dimension: DoubleVector) : DemoModelBase(dimension) {

    override fun createLiveMapSpec(): LiveMapBuilder {
        return basicLiveMap {
            mapLocation = MapLocation.create(GeoRectangle(-71.534745, 42.089492, -70.583055, 42.54947))
        }
    }
}