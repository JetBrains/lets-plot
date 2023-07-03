/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.jetbrains.livemap.chart.donut

import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.datalore.base.typedGeometry.plus
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.jetbrains.livemap.chart.SearchTestHelper
import jetbrains.livemap.Client
import jetbrains.livemap.ClientPoint
import jetbrains.livemap.World
import jetbrains.livemap.chart.ChartElementComponent
import jetbrains.livemap.chart.IndexComponent
import jetbrains.livemap.chart.PieSpecComponent
import jetbrains.livemap.chart.donut.Locator
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.core.ecs.addComponents
import jetbrains.livemap.mapengine.RenderHelper
import jetbrains.livemap.mapengine.placement.WorldOriginComponent
import jetbrains.livemap.mapengine.viewport.Viewport
import jetbrains.livemap.mapengine.viewport.ViewportHelper
import org.junit.Test
import java.util.*
import kotlin.math.PI
import kotlin.math.abs
import kotlin.test.assertEquals

class PieLocatorTest {
    private val viewport = Viewport(ViewportHelper(World.DOMAIN, true, myLoopY = false), ClientPoint(256, 256), 1, 15).apply {
        position = Vec(128, 128)
        zoom = 6
    }
    private val manager = EcsComponentManager()
    private val locator = Locator
    private val r = 10.0
    private val entities = createPie(listOf(2.0, 2.0, 2.0, 2.0))
    private val pieCenter: Vec<Client> = Vec(128, 128)

    private fun createPie(vals: List<Double>): List<EcsEntity> {
        val pies = ArrayList<EcsEntity>()

        manager
            .createEntity("")
            .addComponents {
                +IndexComponent(1, 1)
                +WorldOriginComponent(viewport.getMapCoord(Vec(128, 128)))
                +ChartElementComponent()
                + PieSpecComponent().apply {
                    radius = r
                    indices = vals.indices.toList()
                    sliceValues = transformValues2Angles(vals)
                    colors = vals.indices.map { Color.BLACK }
                }
            }
            .let(pies::add)

        return pies
    }

    private fun checkMouseInPieSector(expectedSector: Int, mouseCoord: Vec<Client>) {
        assertEquals(
            expectedSector,
            SearchTestHelper.getTargetUnderCoord(mouseCoord, locator, entities, RenderHelper(viewport))
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
    fun mouseInFirstPieSector() {
        checkMouseInPieSector(0, pieCenter + Vec(-4, -4))
    }

    @Test
    fun mouseInSecondPieSector() {
        checkMouseInPieSector(1, pieCenter + Vec(4, -4))
        checkMouseInPieSector(1, pieCenter + Vec(9, -1))
    }

    @Test
    fun mouseInThirdPieSector() {
        checkMouseInPieSector(2, pieCenter + Vec(5, 2))
        checkMouseInPieSector(2, pieCenter + Vec(2, 7))
    }

    @Test
    fun mouseInFourthPieSector() {
        checkMouseInPieSector(3, pieCenter + Vec(-4, 4))
        checkMouseInPieSector(3, pieCenter + Vec(-9, 1))
    }

    @Test
    fun mouseOutOfPie() {
        checkMouseInPieSector(SearchTestHelper.UNDEFINED_SECTOR, pieCenter + Vec(10, 7))
        checkMouseInPieSector(SearchTestHelper.UNDEFINED_SECTOR, pieCenter + Vec(9, 14))
    }

    private fun transformValues2Angles(values: List<Double>): List<Double> {
        val sum = values.sumOf(::abs)
        return if (sum == 0.0) {
            MutableList(values.size) { 2 * PI / values.size }
        } else {
            values.map { 2 * PI * abs(it) / sum }
        }
    }
}