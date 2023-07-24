/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.livemap

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.aes.AestheticsBuilder.Companion.constant
import org.jetbrains.letsPlot.core.plot.base.geom.PointGeom
import org.jetbrains.letsPlot.core.plot.base.render.point.NamedShape
import org.jetbrains.letsPlot.core.plot.livemap.ConverterDataHelper.AestheticsDataHelper
import org.jetbrains.letsPlot.core.plot.livemap.ConverterDataHelper.GENERIC_POINTS
import org.jetbrains.letsPlot.core.plot.livemap.ConverterDataHelper.createDefaultMatcher
import org.jetbrains.letsPlot.core.plot.livemap.MapObjectMatcher.Companion.eq
import org.jetbrains.letsPlot.core.plot.livemap.MapObjectMatcher.Companion.sizeEq
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
            .strokeWidth(eq(0.55)) // STROKE_INIT * UNIT_SHAPE_SIZE / 2.0, where STROKE_INIT == 0.5 and UNIT_SHAPE_SIZE == 2.2

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
            .strokeWidth(eq(0.55))

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