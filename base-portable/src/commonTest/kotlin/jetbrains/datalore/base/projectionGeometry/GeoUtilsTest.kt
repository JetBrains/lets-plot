package jetbrains.datalore.base.projectionGeometry

import jetbrains.datalore.base.projectionGeometry.GeoRectangleTestHelper.assertRectangleEquals
import jetbrains.datalore.base.projectionGeometry.GeoUtils.EARTH_RECT
import jetbrains.datalore.base.projectionGeometry.GeoUtils.calculateQuadKeys
import jetbrains.datalore.base.projectionGeometry.GeoUtils.getQuadKeyRect
import jetbrains.datalore.base.projectionGeometry.GeoUtils.tileXYToTileID
import kotlin.test.Test
import kotlin.test.assertEquals

class GeoUtilsTest {
    private fun quadKeys(vararg keys: String): Set<QuadKey> {
        val quadKeys = HashSet<QuadKey>()
        for (key in keys) {
            quadKeys.add(QuadKey(key))
        }
        return quadKeys
    }

    @Test
    fun calculateTexasQuadKey() {
        val bbox = LonLatRectangle(-107.0, 25.0, 14.0, 12.0)
        val tileIDs = calculateQuadKeys(bbox, 5)
        assertEquals(tileIDs, quadKeys("02130", "02112", "02133", "02132", "02131", "02113"))
    }

    @Test
    fun calculateZeroTileBBox() {
        val rect = getQuadKeyRect(QuadKey("0"))
        val expectedRect = Rectangle(
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
        assertEquals("0123", tileXYToTileID(5, 3, zoom))
    }
}
