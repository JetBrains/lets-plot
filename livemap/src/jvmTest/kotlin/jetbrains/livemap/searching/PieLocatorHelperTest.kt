/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.jetbrains.livemap.searching

import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.datalore.base.typedGeometry.explicitVec
import jetbrains.datalore.jetbrains.livemap.searching.SearchTestHelper.UNDEFINED_SECTOR
import jetbrains.datalore.jetbrains.livemap.searching.SearchTestHelper.getTargetUnderCoord
import jetbrains.datalore.jetbrains.livemap.searching.SearchTestHelper.point
import jetbrains.livemap.api.transformValues2Angles
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.core.ecs.addComponents
import jetbrains.livemap.placement.ScreenLoopComponent
import jetbrains.livemap.projection.Client
import jetbrains.livemap.rendering.PieSectorComponent
import jetbrains.livemap.searching.PieLocatorHelper
import org.junit.Test
import java.util.*
import kotlin.math.PI
import kotlin.test.assertEquals

class PieLocatorHelperTest {
    private val manager = EcsComponentManager()
    private val helper = PieLocatorHelper()
    private val r = 10.0
    private val entities = createPie(listOf(2.0, 2.0, 2.0, 2.0))

    private fun createPie(values: List<Double>): List<EcsEntity> {
        val angles = transformValues2Angles(values)
        var currentAngle = - PI / 2
        val pies = ArrayList<EcsEntity>()

        for (i in angles.indices) {
            val startAngle = currentAngle
            val endAngle = currentAngle + angles[i]

            manager
                .createEntity("")
                .addComponents {
                    + ScreenLoopComponent().apply { origins = listOf(explicitVec(0.0, 0.0)) }
                    + PieSectorComponent().apply {
                        this.radius = r
                        this.startAngle = startAngle
                        this.endAngle = endAngle
                    }
                }
                .let(pies::add)
            currentAngle = endAngle
        }

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
    fun mouseInFirstPieSector() {
        checkMouseInPieSector(0, point(4, -4))
    }

    @Test
    fun mouseInSecondPieSector() {
        checkMouseInPieSector(1, point(4, 4))
        checkMouseInPieSector(1, point(9, 1))
    }

    @Test
    fun mouseInThirdPieSector() {
        checkMouseInPieSector(2, point(-5, 2))
        checkMouseInPieSector(2, point(-2, 7))
    }

    @Test
    fun mouseInFourthPieSector() {
        checkMouseInPieSector(3, point(-4, -4))
        checkMouseInPieSector(3, point(-10, 0))
    }

    @Test
    fun mouseOutOfPie() {
        checkMouseInPieSector(UNDEFINED_SECTOR, point(10, 7))
        checkMouseInPieSector(UNDEFINED_SECTOR, point(9, 14))
    }
}