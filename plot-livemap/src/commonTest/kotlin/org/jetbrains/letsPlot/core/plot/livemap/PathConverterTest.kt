/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.livemap

import org.jetbrains.letsPlot.core.plot.base.aes.AestheticsBuilder.Companion.constant
import org.jetbrains.letsPlot.core.plot.base.geom.PathGeom
import org.jetbrains.letsPlot.core.plot.base.render.linetype.NamedLineType
import org.jetbrains.letsPlot.core.plot.livemap.ConverterDataHelper.AestheticsDataHelper
import org.jetbrains.letsPlot.core.plot.livemap.ConverterDataHelper.GENERIC_POINTS
import org.jetbrains.letsPlot.core.plot.livemap.ConverterDataHelper.PATH
import org.jetbrains.letsPlot.core.plot.livemap.ConverterDataHelper.createDefaultMatcher
import org.jetbrains.letsPlot.core.plot.livemap.MapObjectMatcher.Companion.eq
import org.jetbrains.letsPlot.core.plot.livemap.MapObjectMatcher.Companion.geometryEq
import org.jetbrains.letsPlot.core.plot.livemap.MapObjectMatcher.Companion.sizeEq
import org.jetbrains.letsPlot.core.plot.livemap.MapObjectMatcher.Companion.vectorEq
import org.jetbrains.letsPlot.gis.geoprotocol.Boundary
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
            .geometry(geometryEq(Boundary.create(PATH)))
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
            .strokeWidth(eq(1.1))
            .lineDash(vectorEq(listOf(4.4, 4.4)))

        assertMapObject()
    }

    @Test
    fun dashedLineWithSizeShouldBeInitializedProperly() {
        aesData!!.builder()
            .lineType(constant(NamedLineType.DASHED))
            .size(constant(2.0))

        matcher!!
            .strokeWidth(eq(4.4))
            .lineDash(vectorEq(listOf(17.6, 17.6)))

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