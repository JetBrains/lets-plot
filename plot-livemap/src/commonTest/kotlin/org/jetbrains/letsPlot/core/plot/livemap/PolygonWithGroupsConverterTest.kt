/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.livemap

import org.jetbrains.letsPlot.core.plot.livemap.ConverterDataHelper.AestheticsDataHelper
import org.jetbrains.letsPlot.core.plot.livemap.ConverterDataHelper.FIRST_RING
import org.jetbrains.letsPlot.core.plot.livemap.ConverterDataHelper.SECOND_RING
import org.jetbrains.letsPlot.core.plot.livemap.ConverterDataHelper.createDefaultMatcher
import org.jetbrains.letsPlot.core.plot.livemap.ConverterDataHelper.multiPolygon
import org.jetbrains.letsPlot.core.plot.livemap.ConverterDataHelper.polygon
import org.jetbrains.letsPlot.core.plot.livemap.MapObjectMatcher.Companion.geometryEq
import org.jetbrains.letsPlot.core.plot.livemap.MapObjectMatcher.Companion.sizeEq
import org.jetbrains.letsPlot.gis.geoprotocol.Boundary
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
        matcher.geometry(geometryEq(Boundary.create(multiPolygon(polygon(FIRST_RING)))))
        assertMapObject(0)

        matcher.geometry(geometryEq(Boundary.create(multiPolygon(polygon(SECOND_RING)))))
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