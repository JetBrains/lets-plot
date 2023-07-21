/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.chart.donut

import org.jetbrains.letsPlot.commons.intern.typedGeometry.Vec
import org.jetbrains.letsPlot.commons.intern.typedGeometry.plus
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.livemap.chart.SearchTestHelper
import org.jetbrains.letsPlot.livemap.Client
import org.jetbrains.letsPlot.livemap.ClientPoint
import org.jetbrains.letsPlot.livemap.World
import org.jetbrains.letsPlot.livemap.chart.ChartElementComponent
import org.jetbrains.letsPlot.livemap.chart.IndexComponent
import org.jetbrains.letsPlot.livemap.chart.PieSpecComponent
import org.jetbrains.letsPlot.livemap.chart.donut.Locator
import org.jetbrains.letsPlot.livemap.core.ecs.EcsComponentManager
import org.jetbrains.letsPlot.livemap.core.ecs.EcsEntity
import org.jetbrains.letsPlot.livemap.core.ecs.addComponents
import org.jetbrains.letsPlot.livemap.mapengine.RenderHelper
import org.jetbrains.letsPlot.livemap.mapengine.placement.WorldOriginComponent
import org.jetbrains.letsPlot.livemap.mapengine.viewport.Viewport
import org.jetbrains.letsPlot.livemap.mapengine.viewport.ViewportHelper
import org.junit.Test
import java.util.*
import kotlin.math.PI
import kotlin.math.abs
import kotlin.test.assertEquals

class PieLocatorTest {
    private val viewport = Viewport(ViewportHelper(org.jetbrains.letsPlot.livemap.World.DOMAIN, true, myLoopY = false),
        org.jetbrains.letsPlot.livemap.ClientPoint(256, 256), 1, 15).apply {
        position = Vec(128, 128)
        zoom = 6
    }
    private val manager = EcsComponentManager()
    private val locator = Locator
    private val r = 10.0
    private val entities = createPie(listOf(2.0, 2.0, 2.0, 2.0))
    private val pieCenter: Vec<org.jetbrains.letsPlot.livemap.Client> = Vec(128, 128)

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
                    fillColors = vals.indices.map { Color.BLACK }
                }
            }
            .let(pies::add)

        return pies
    }

    private fun checkMouseInPieSector(expectedSector: Int, mouseCoord: Vec<org.jetbrains.letsPlot.livemap.Client>) {
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