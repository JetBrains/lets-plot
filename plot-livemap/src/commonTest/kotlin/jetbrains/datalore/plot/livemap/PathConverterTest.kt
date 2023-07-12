/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.livemap

import org.jetbrains.letsPlot.core.plot.base.aes.AestheticsBuilder.Companion.constant
import org.jetbrains.letsPlot.core.plot.base.geom.PathGeom
import org.jetbrains.letsPlot.core.plot.base.render.linetype.NamedLineType
import jetbrains.datalore.plot.livemap.ConverterDataHelper.AestheticsDataHelper
import jetbrains.datalore.plot.livemap.ConverterDataHelper.GENERIC_POINTS
import jetbrains.datalore.plot.livemap.ConverterDataHelper.PATH
import jetbrains.datalore.plot.livemap.ConverterDataHelper.createDefaultMatcher
import jetbrains.datalore.plot.livemap.MapObjectMatcher.Companion.eq
import jetbrains.datalore.plot.livemap.MapObjectMatcher.Companion.geometryEq
import jetbrains.datalore.plot.livemap.MapObjectMatcher.Companion.sizeEq
import jetbrains.datalore.plot.livemap.MapObjectMatcher.Companion.vectorEq
import jetbrains.gis.geoprotocol.Boundary
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