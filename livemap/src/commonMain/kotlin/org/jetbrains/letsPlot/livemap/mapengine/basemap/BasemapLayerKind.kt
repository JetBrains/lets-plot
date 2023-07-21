/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.mapengine.basemap

enum class BasemapLayerKind constructor(private val myValue: String) {
    WORLD("world"),
    LABEL("label"),
    DEBUG("debug"),
    RASTER("raster_tile");

    override fun toString(): String {
        return myValue
    }
}
