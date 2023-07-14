/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.livemap

import org.jetbrains.letsPlot.commons.intern.typedGeometry.explicitVec
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.aes.AesInitValue
import org.jetbrains.letsPlot.core.plot.base.aes.AestheticsBuilder.Companion.constant
import org.jetbrains.letsPlot.core.plot.base.geom.TextGeom
import jetbrains.datalore.plot.livemap.ConverterDataHelper.AestheticsDataHelper
import jetbrains.datalore.plot.livemap.ConverterDataHelper.createDefaultMatcher
import jetbrains.datalore.plot.livemap.MapObjectMatcher.Companion.eq
import jetbrains.datalore.plot.livemap.MapObjectMatcher.Companion.sizeEq
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

        aesData.builder().hjust(constant(0.0))
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

        aesData.builder().hjust(constant(1.0))
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
        val mapObjectList = aesData.buildConverter().toText(TextGeom())
        assertEquals(1, mapObjectList.size)

        matcher.match(mapObjectList[0])
    }
}