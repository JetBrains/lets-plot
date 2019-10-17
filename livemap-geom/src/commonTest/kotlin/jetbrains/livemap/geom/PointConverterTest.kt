package jetbrains.livemap.geom

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.aes.AestheticsBuilder.Companion.constant
import jetbrains.datalore.plot.base.geom.PointGeom
import jetbrains.datalore.plot.base.render.point.NamedShape
import jetbrains.livemap.geom.ConverterDataHelper.AestheticsDataHelper
import jetbrains.livemap.geom.ConverterDataHelper.GENERIC_POINTS
import jetbrains.livemap.geom.ConverterDataHelper.createDefaultMatcher
import jetbrains.livemap.geom.MapObjectMatcher.Companion.eq
import jetbrains.livemap.geom.MapObjectMatcher.Companion.sizeEq
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class PointConverterTest {
    private val aesData: AestheticsDataHelper = AestheticsDataHelper.create()
    private val matcher: MapObjectMatcher = createDefaultMatcher()

    @BeforeTest
    fun setUp() {
        aesData.addGroup(GENERIC_POINTS)
    }

    @Test
    fun shouldConvertCommonMapObjectProperties() {
        val fill = Color(1, 2, 3, 4)
        val color = Color(5, 6, 7, 8)
        aesData.builder()
            .fill(constant(fill))
            .color(constant(color))

        matcher
            .fillColor(eq(fill))
            .strokeColor(eq(color))

        assertMapObject(0)
        assertMapObject(1)
    }

    @Test
    fun smallSizeShape_ShouldReduceRadiusAndSetStrokeWidthToOne() {
        aesData.builder()
            .shape(constant(NamedShape.BULLET))
            .size(constant(4.0))

        matcher
            .radius(eq(3.0))
            .strokeWidth(eq(1.0))

        assertMapObject(0)
        assertMapObject(1)
    }

    @Test
    fun normalSizeShape_ShouldIncreaseRadiusAdSetStrokeWidthToOne() {
        aesData.builder()
            .shape(constant(NamedShape.FILLED_CIRCLE))
            .size(constant(4.0))

        matcher
            .radius(eq(5.0))
            .strokeWidth(eq(1.0))

        assertMapObject(0)
        assertMapObject(1)
    }

    @Test
    fun pointGeometryShouldBeInMercator() {
        assertPointGeometryDataArray()
    }

    @Test
    fun shapeShouldBeAppliedToEveryMapObject() {
        aesData.builder().shape(constant(NamedShape.FILLED_CIRCLE))

        matcher.shape(eq(NamedShape.FILLED_CIRCLE.code))

        assertMapObject(0)
        assertMapObject(1)
    }

    @Test
    fun eachPoint_ShouldContainOneBoundingBoxes() {
        matcher.locationBoundingBoxes(sizeEq(1))

        assertMapObject(0)
        assertMapObject(1)
    }

    @Test
    fun whenMapIdSet_ShouldUseGeometry() {
        aesData.builder().mapId(constant("New York City"))

        assertPointGeometryDataArray()
    }

    private fun assertMapObject(index: Int) {
        val mapObjectList = aesData.buildConverter().toPoint(PointGeom())
        assertEquals(2, mapObjectList.size.toLong())
        matcher.match(mapObjectList[index])
    }

    private fun assertPointGeometryDataArray() {
        matcher.point(eq(GENERIC_POINTS[0]))
        assertMapObject(0)

        matcher.point(eq(GENERIC_POINTS[1]))
        assertMapObject(1)
    }
}