package jetbrains.livemap.geom

import jetbrains.gis.geoprotocol.Geometry
import jetbrains.livemap.geom.ConverterDataHelper.AestheticsDataHelper
import jetbrains.livemap.geom.ConverterDataHelper.FIRST_RING
import jetbrains.livemap.geom.ConverterDataHelper.SECOND_RING
import jetbrains.livemap.geom.ConverterDataHelper.createDefaultMatcher
import jetbrains.livemap.geom.ConverterDataHelper.multiPolygon
import jetbrains.livemap.geom.ConverterDataHelper.polygon
import jetbrains.livemap.geom.MapObjectMatcher.Companion.geometryEq
import jetbrains.livemap.geom.MapObjectMatcher.Companion.sizeEq
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class PolygonWithGroupsConverterTest {
    private var aesData: AestheticsDataHelper = AestheticsDataHelper.create()
    private var matcher: MapObjectMatcher = createDefaultMatcher()

    @BeforeTest
    fun setUp() {
        aesData.addGroup(FIRST_RING)
        aesData.addGroup(SECOND_RING)
    }

    @Test
    fun shouldProperlyConvertMultiPath() {
        matcher.geometry(geometryEq(Geometry.create(multiPolygon(polygon(FIRST_RING)))))
        assertMapObject(0)

        matcher.geometry(geometryEq(Geometry.create(multiPolygon(polygon(SECOND_RING)))))
        assertMapObject(1)
    }

    @Test
    fun eachRing_ShouldContainOneBoundingBox() {
        matcher.locationBoundingBoxes(sizeEq(1))

        assertMapObject(0)
        assertMapObject(1)
    }

    private fun assertMapObject(index: Int) {
        val mapObjectList = aesData.buildConverter().toPolygon()
        assertEquals(2, mapObjectList.size)
        matcher.match(mapObjectList[index])
    }
}