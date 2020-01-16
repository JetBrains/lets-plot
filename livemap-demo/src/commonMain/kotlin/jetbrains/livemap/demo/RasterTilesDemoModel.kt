/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.demo

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.livemap.api.LiveMapBuilder
import jetbrains.livemap.config.DevParams

class RasterTilesDemoModel(dimension: DoubleVector) : DemoModelBase(dimension) {

    override fun createLiveMapSpec(): LiveMapBuilder {
        return basicLiveMap {
            params(
                DevParams.RASTER_TILES.key to mapOf(
                    "host" to "c.tile.stamen.com/toner",
                    "protocol" to "http",
                    "format" to "/\${z}/\${x}/\${y}@2x.png"
                )
            )
        }
    }
}