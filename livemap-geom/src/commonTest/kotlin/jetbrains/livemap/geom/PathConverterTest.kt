package jetbrains.livemap.geom

import jetbrains.datalore.plot.base.aes.AestheticsBuilder.Companion.constant
import jetbrains.datalore.plot.base.geom.PathGeom
import jetbrains.datalore.plot.base.render.linetype.NamedLineType
import jetbrains.gis.geoprotocol.Geometry
import jetbrains.livemap.geom.ConverterDataHelper.AestheticsDataHelper
import jetbrains.livemap.geom.ConverterDataHelper.GENERIC_POINTS
import jetbrains.livemap.geom.ConverterDataHelper.PATH
import jetbrains.livemap.geom.ConverterDataHelper.createDefaultMatcher
import jetbrains.livemap.geom.MapObjectMatcher.Companion.eq
import jetbrains.livemap.geom.MapObjectMatcher.Companion.geometryEq
import jetbrains.livemap.geom.MapObjectMatcher.Companion.sizeEq
import jetbrains.livemap.geom.MapObjectMatcher.Companion.vectorEq
import kotlin.test.BeforeTest
import kotlin.test.Test

class PathConverterTest {
    private var aesData: AestheticsDataHelper? = null
    private var matcher: MapObjectMatcher? = null

    @BeforeTest
    fun setUp() {
        aesData = AestheticsDataHelper.create()
        aesData!!.addGroup(GENERIC_POINTS)

        matcher = createDefaultMatcher()
            .geometry(geometryEq(Geometry.create(PATH)))
    }

    @Test
    fun blankLineShouldNotHaveDashPattern() {
        aesData!!.builder().lineType(constant(NamedLineType.BLANK))

        matcher!!.lineDash(vectorEq(emptyList()))

        assertMapObject()
    }

    @Test
    fun dashedLineWithoutSizeShouldBeInitializedProperly() {
        aesData!!.builder().lineType(constant(NamedLineType.DASHED))

        matcher!!
            .strokeWidth(eq(1.0))
            .lineDash(vectorEq(listOf(4.3, 4.3)))

        assertMapObject()
    }

    @Test
    fun dashedLineWithSizeShouldBeInitializedProperly() {
        aesData!!.builder()
            .lineType(constant(NamedLineType.DASHED))
            .size(constant(2.0))

        matcher!!
            .strokeWidth(eq(4.0))
            .lineDash(vectorEq(listOf(17.2, 17.2)))

        assertMapObject()
    }

    @Test
    fun path_ShouldContainOneBoundingBoxes() {
        matcher!!.locationBoundingBoxes(sizeEq(1))

        assertMapObject()
    }

    private fun assertMapObject() {
        val mapObjectList = aesData!!.buildConverter().toPath(PathGeom())
        matcher!!.match(mapObjectList[0])
    }

}