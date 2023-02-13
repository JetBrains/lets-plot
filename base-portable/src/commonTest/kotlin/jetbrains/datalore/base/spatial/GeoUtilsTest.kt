/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.spatial

import jetbrains.datalore.base.spatial.GeoRectangleTestHelper.assertRectangleEquals
import jetbrains.datalore.base.typedGeometry.*
import kotlin.test.Test
import kotlin.test.assertEquals

class GeoUtilsTest {
    private fun quadKeys(vararg keys: String): Set<QuadKey<LonLat>> {
        val quadKeys = HashSet<QuadKey<LonLat>>()
        for (key in keys) {
            quadKeys.add(
                QuadKey<LonLat>(
                    key
                )
            )
        }
        return quadKeys
    }

    @Test
    fun calculateTexasQuadKey() {
        val bbox = Rect.XYWH<LonLat>(-107.0, 25.0, 14.0, 12.0)
        val tileIDs = calculateQuadKeys(bbox, 5)
        assertEquals(tileIDs, quadKeys("02130", "02112", "02133", "02132", "02131", "02113"))
    }

    @Test
    fun calculateZeroTileBBox() {
        val rect = QuadKey<LonLat>(
            "0"
        ).computeRect()
        val expectedRect = Rect.XYWH<Untyped>(
                EARTH_RECT.left,
                EARTH_RECT.center.x,
                EARTH_RECT.width / 2,
                EARTH_RECT.height / 2
        )
        assertRectangleEquals(expectedRect, rect)
    }

    @Test
    fun calculateBostonTileIdByCentroid() {
        val zoom = 4
        assertEquals("0123", xyToKey(5, 3, zoom))
    }
}
