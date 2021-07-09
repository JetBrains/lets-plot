/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.basemap.raster

import jetbrains.livemap.viewport.CellKey
import jetbrains.livemap.basemap.raster.RasterTileLoadingSystem.Companion.replacePlaceholders
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