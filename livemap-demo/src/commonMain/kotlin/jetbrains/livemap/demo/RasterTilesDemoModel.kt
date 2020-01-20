/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.demo

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.livemap.api.LiveMapBuilder
import jetbrains.livemap.config.TileParameters
import jetbrains.livemap.tiles.TileLoadingSystemFactory

class RasterTilesDemoModel(dimension: DoubleVector) : DemoModelBase(dimension) {

    override fun createLiveMapSpec(): LiveMapBuilder {
        return basicLiveMap {
            tileLoadingSystemFactory =
                TileLoadingSystemFactory.createTileLoadingFactory(
                    TileParameters(mapOf("raster" to "http://c.tile.stamen.com/toner/{z}/{x}/{y}@2x.png")),
                    false,
                    1000)
        }
    }
}