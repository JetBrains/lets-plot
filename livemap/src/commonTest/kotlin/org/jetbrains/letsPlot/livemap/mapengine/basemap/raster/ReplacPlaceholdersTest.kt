/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.mapengine.basemap.raster

import org.jetbrains.letsPlot.livemap.mapengine.basemap.raster.RasterTileLoadingSystem.Companion.replacePlaceholders
import org.jetbrains.letsPlot.livemap.mapengine.viewport.CellKey
import kotlin.test.Test
import kotlin.test.assertEquals

class ReplacPlaceholdersTest {

    @Test
    fun foo() {
        val format = "/{z}/{x}/{y}.png"

        assertEquals("/1/0/0.png", replacePlaceholders(CellKey("0"), format))
        assertEquals("/2/0/0.png", replacePlaceholders(CellKey("00"), format))
        assertEquals("/2/2/2.png", replacePlaceholders(CellKey("30"), format))
    }
}