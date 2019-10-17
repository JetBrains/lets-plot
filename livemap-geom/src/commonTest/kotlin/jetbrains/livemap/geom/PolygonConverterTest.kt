package jetbrains.livemap.geom

import jetbrains.datalore.plot.base.aes.AestheticsBuilder.Companion.constant
import jetbrains.gis.geoprotocol.Geometry
import jetbrains.livemap.geom.ConverterDataHelper.AestheticsDataHelper
import jetbrains.livemap.geom.ConverterDataHelper.MULTIPOLYGON
import jetbrains.livemap.geom.ConverterDataHelper.createDefaultMatcher
import jetbrains.livemap.geom.ConverterDataHelper.rings
import jetbrains.livemap.geom.MapObjectMatcher.Companion.geometryEq
import jetbrains.livemap.geom.MapObjectMatcher.Companion.sizeEq
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class PolygonConverterTest {
    private var aesData: AestheticsDataHelper = AestheticsDataHelper.create()
    private var matcher: MapObjectMatcher = createDefaultMatcher()

    @BeforeTest
    fun setUp() {
        aesData.addGroup(rings())
    }

    @Test
    fun multiPathShouldBeConvertedProperlyIntoMercator() {

        matcher.geometry(geometryEq(Geometry.create(MULTIPOLYGON)))

        assertMapObject()
    }

    @Test
    fun withMapIdAndGeometry_ShouldUseGeometry() {
        aesData.builder().mapId(constant("New York City"))

        matcher.geometry(geometryEq(Geometry.create(MULTIPOLYGON)))

        assertMapObject()
    }

    @Test
    fun twoRings_ShouldContainOneBoundingBox() {
        matcher.locationBoundingBoxes(sizeEq(1))

        assertMapObject()
    }

    private fun assertMapObject() {
        val mapObjectList = aesData.buildConverter().toPolygon()
        assertEquals(1, mapObjectList.size)
        matcher.match(mapObjectList[0])
    }
}