/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.livemap

import jetbrains.datalore.plot.livemap.ConverterDataHelper.AestheticsDataHelper
import jetbrains.datalore.plot.livemap.ConverterDataHelper.MULTIPOLYGON
import jetbrains.datalore.plot.livemap.ConverterDataHelper.createDefaultMatcher
import jetbrains.datalore.plot.livemap.ConverterDataHelper.rings
import jetbrains.datalore.plot.livemap.MapObjectMatcher.Companion.geometryEq
import jetbrains.datalore.plot.livemap.MapObjectMatcher.Companion.sizeEq
import org.jetbrains.letsPlot.gis.geoprotocol.Boundary
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

        matcher.geometry(geometryEq(Boundary.create(MULTIPOLYGON)))

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