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
                    "https://a.tile.stamen.com/toner/{z}/{x}/{y}@2x.png",
                    "https://b.tile.stamen.com/toner/{z}/{x}/{y}@2x.png",
                    "https://c.tile.stamen.com/toner/{z}/{x}/{y}@2x.png"
                )
            )
            attribution = "© stamen.com"
        }
    }
}