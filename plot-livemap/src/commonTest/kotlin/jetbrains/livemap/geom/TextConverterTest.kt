package jetbrains.livemap.geom

import jetbrains.datalore.base.projectionGeometry.explicitVec
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.aes.AesInitValue
import jetbrains.datalore.plot.base.aes.AestheticsBuilder.Companion.constant
import jetbrains.livemap.geom.ConverterDataHelper.AestheticsDataHelper
import jetbrains.livemap.geom.ConverterDataHelper.createDefaultMatcher
import jetbrains.livemap.geom.MapObjectMatcher.Companion.eq
import jetbrains.livemap.geom.MapObjectMatcher.Companion.sizeEq
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class TextConverterTest {
    private var aesData: AestheticsDataHelper = AestheticsDataHelper.create()
    private var matcher: MapObjectMatcher = createDefaultMatcher()

    @BeforeTest
    fun setUp() {
        aesData.addGroup(listOf(explicitVec(0.0, 0.0)))

    }

    @Test
    fun textSize() {
        aesData.builder().size(constant(2.0))

        matcher.radius(eq(4.0))

        assertMapObject()
    }

    @Test
    fun whenFontFaceIsDefault_ShouldReturnEmptyString() {
        aesData.builder().fontface(constant(AesInitValue[Aes.FONTFACE]))

        matcher.fontface(eq(""))

        assertMapObject()
    }

    @Test
    fun whenFontFaceIsNotDefault_ShouldReturnValue() {
        aesData.builder().fontface(constant("bold"))

        matcher.fontface(eq("bold"))

        assertMapObject()
    }

    @Test
    fun whenSizeSet_RadiusShouldBeMultiplied_AndStrokeWidthShouldBeZero() {
        aesData.builder().size(constant(2.0))

        matcher
            .radius(eq(4.0))
            .strokeWidth(eq(0.0))

        assertMapObject()
    }

    @Test
    fun casesWhenHjustShouldBeZero() {
        matcher.hjust(eq(0.0))

        aesData.builder().hjust(constant("left"))
        assertMapObject()

        aesData.builder().hjust(constant(1.0))
        assertMapObject()
    }

    @Test
    fun casesWhenHjustShouldBeZeroFive() {
        matcher.hjust(eq(0.5))

        aesData.builder().hjust(constant("middle"))
        assertMapObject()

        aesData.builder().hjust(constant(0.5))
        assertMapObject()
    }

    @Test
    fun casesWhenHjustShouldBeOne() {
        matcher.hjust(eq(1.0))

        aesData.builder().hjust(constant("right"))
        assertMapObject()

        aesData.builder().hjust(constant(0.0))
        assertMapObject()
    }

    @Test
    fun casesWhenVjustShouldBeZero() {
        matcher.vjust(eq(0.0))

        aesData.builder().vjust(constant("top"))
        assertMapObject()

        aesData.builder().hjust(constant(1.0))
        assertMapObject()
    }

    @Test
    fun casesWhenVjustShouldBeZeroFive() {
        matcher.vjust(eq(0.5))

        aesData.builder().hjust(constant("center"))
        assertMapObject()

        aesData.builder().hjust(constant(0.5))
        assertMapObject()
    }

    @Test
    fun casesWhenVjustShouldBeOne() {
        matcher.vjust(eq(1.0))

        aesData.builder().vjust(constant("bottom"))
        assertMapObject()

        aesData.builder().hjust(constant(0.0))
        assertMapObject()
    }

    @Test
    fun text_ShouldContainOneBoundingBoxes() {
        matcher.locationBoundingBoxes(sizeEq(1))

        assertMapObject()
    }

    private fun assertMapObject() {
        val mapObjectList = aesData.buildConverter().toText()
        assertEquals(1, mapObjectList.size)

        matcher.match(mapObjectList[0])
    }
}