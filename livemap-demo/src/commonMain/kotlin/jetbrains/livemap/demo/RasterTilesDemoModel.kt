/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.demo

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import jetbrains.livemap.api.LiveMapBuilder
import jetbrains.livemap.mapengine.basemap.Tilesets

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