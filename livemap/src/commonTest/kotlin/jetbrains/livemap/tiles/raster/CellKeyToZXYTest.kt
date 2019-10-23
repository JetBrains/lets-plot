package jetbrains.livemap.tiles.raster

import jetbrains.livemap.projections.CellKey
import jetbrains.livemap.tiles.raster.RasterTileLoadingSystem.Companion.getZXY
import kotlin.test.Test
import kotlin.test.assertEquals

class CellKeyToZXYTest {

    @Test
    fun foo() {
        val format = "/\${z}/\${x}/\${y}.png"

        assertEquals("/1/0/0.png", getZXY(CellKey("0"), format))
        assertEquals("/2/0/0.png", getZXY(CellKey("00"), format))
        assertEquals("/2/2/2.png", getZXY(CellKey("30"), format))
    }
}