/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.jetbrains.livemap.searching

import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.datalore.base.typedGeometry.explicitVec
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.jetbrains.livemap.searching.SearchTestHelper.UNDEFINED_SECTOR
import jetbrains.datalore.jetbrains.livemap.searching.SearchTestHelper.getTargetUnderCoord
import jetbrains.datalore.jetbrains.livemap.searching.SearchTestHelper.point
import jetbrains.livemap.Client
import jetbrains.livemap.api.transformValues2Angles
import jetbrains.livemap.chart.ChartElementComponent
import jetbrains.livemap.chart.DonutChart
import jetbrains.livemap.chart.SymbolComponent
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.core.ecs.addComponents
import jetbrains.livemap.mapengine.placement.ScreenLoopComponent
import jetbrains.livemap.searching.IndexComponent
import org.junit.Ignore
import org.junit.Test
import java.util.*
import kotlin.test.assertEquals

class PieLocatorHelperTest {
    private val manager = EcsComponentManager()
    private val helper = DonutChart.Locator()
    private val r = 10.0
    private val entities = createPie(listOf(2.0, 2.0, 2.0, 2.0))

    private fun createPie(vals: List<Double>): List<EcsEntity> {
        val pies = ArrayList<EcsEntity>()

        manager
            .createEntity("")
            .addComponents {
                + IndexComponent(1, 1)
                + ChartElementComponent()
                + SymbolComponent().apply {
                    size = explicitVec(r * 2, r * 2)
                    indices = vals.indices.toList()
                    values = transformValues2Angles(vals)
                    colors = vals.indices.map { Color.BLACK }
                }
                + ScreenLoopComponent().apply { origins = listOf(explicitVec(0.0, 0.0)) }
            }
            .let(pies::add)

        return pies
    }

    private fun checkMouseInPieSector(expectedSector: Int, mouseCoord: Vec<Client>) {
        assertEquals(
            expectedSector,
            getTargetUnderCoord(mouseCoord, helper, entities)
        )
    }

    @Test
    fun calculateAngles() {
        assertEquals(
            listOf(
                Math.toRadians(60.0),
                Math.toRadians(60.0),
                Math.toRadians(120.0),
                Math.toRadians(60.0),
                Math.toRadians(60.0)
            ),
            transformValues2Angles(listOf(3.0, -3.0, 6.0, 3.0, -3.0))
        )
    }

    @Test
    fun calculateAnglesForZeroValues() {
        val count = 5
        assertEquals(
            Collections.nCopies(count, 2 * Math.PI / count),
            transformValues2Angles(Collections.nCopies(count, 0.0))
        )
    }

    @Test
    @Ignore // ToDo Fix test
    fun mouseInFirstPieSector() {
        checkMouseInPieSector(0, point(4, -4))
    }

    @Test
    @Ignore // ToDo Fix test
    fun mouseInSecondPieSector() {
        checkMouseInPieSector(1, point(4, 4))
        checkMouseInPieSector(1, point(9, 1))
    }

    @Test
    @Ignore // ToDo Fix test
    fun mouseInThirdPieSector() {
        checkMouseInPieSector(2, point(-5, 2))
        checkMouseInPieSector(2, point(-2, 7))
    }

    @Test
    @Ignore // ToDo Fix test
    fun mouseInFourthPieSector() {
        checkMouseInPieSector(3, point(-4, -4))
        checkMouseInPieSector(3, point(-10, 0))
    }

    @Test
    @Ignore // ToDo Fix test
    fun mouseOutOfPie() {
        checkMouseInPieSector(UNDEFINED_SECTOR, point(10, 7))
        checkMouseInPieSector(UNDEFINED_SECTOR, point(9, 14))
    }
}