/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.demo

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.livemap.api.LiveMapBuilder
import jetbrains.livemap.basemap.Tilesets

class SolidColorTilesDemoModel(dimension: DoubleVector) : DemoModelBase(dimension) {

    override fun createLiveMapSpec(): LiveMapBuilder {
        return basicLiveMap {
            tileSystemProvider = Tilesets.chessboard()
        }
    }
}