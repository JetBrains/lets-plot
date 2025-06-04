/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.livemap.common.component

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.livemap.api.LiveMapBuilder
import org.jetbrains.letsPlot.livemap.mapengine.basemap.Tilesets

class RasterTilesDemoModel(dimension: DoubleVector) : DemoModelBase(dimension) {

    override fun createLiveMapSpec(): LiveMapBuilder {
        return basicLiveMap {
            tileSystemProvider = Tilesets.raster(
                listOf(
                    "https://tile.opentopomap.org/{z}/{x}/{y}.png"
                )
            )
            attribution = "map data: <a href=\"https://www.openstreetmap.org/copyright\">© OpenStreetMap contributors</a>, <a href=\"http://viewfinderpanoramas.org/\">SRTM</a> | map style: <a href=\"https://opentopomap.org/\">© OpenTopoMap</a> (<a href=\"https://creativecommons.org/licenses/by-sa/3.0/\">CC-BY-SA</a>) "
        }
    }
}