/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.tooltip

import org.jetbrains.letsPlot.core.plot.base.tooltip.TooltipHint.Placement.*
import kotlin.test.BeforeTest
import kotlin.test.Test

class TooltipModelFactoryHintShapeTest : TooltipModelTestHelper() {

    @BeforeTest
    fun setUp() {
        init()

        // Add mapping otherwise hint will not be created
        addMappedData(MappedDataAccessMock.variable().mapping(AES_WIDTH))
    }

    @Test
    fun withPointHitShape_ShouldAddHintAroundPoint() {
        createTooltipModels(geomTargetBuilder.withPointHitShape(TARGET_HIT_COORD, 0.0).build())

        assertHint(
            VERTICAL,
            TARGET_HIT_COORD,
            DEFAULT_OBJECT_RADIUS
        )
    }

    @Test
    fun withPathHitShape_ShouldAddHintMiddleAtY() {
        createTooltipModels(geomTargetBuilder.withPathHitShape().build())

        assertHint(
            HORIZONTAL,
            TARGET_HIT_COORD,
            DEFAULT_OBJECT_RADIUS
        )
    }

    @Test
    fun withPolygonHitShape_ShouldAddHintUnderCursor() {
        createTooltipModels(geomTargetBuilder.withPolygonHitShape(CURSOR_COORD).build())

        assertHint(
            CURSOR,
            CURSOR_COORD,
            DEFAULT_OBJECT_RADIUS
        )
    }

    @Test
    fun withRectHitShape_ShouldAddHintMiddleAtY() {
        val dim = TestUtil.size(10.0, 12.0)

        createTooltipModels(geomTargetBuilder.withRectHitShape(TestUtil.rect(TARGET_HIT_COORD, dim)).build())

        val radius = dim.x / 2
        assertHint(
            HORIZONTAL,
            TARGET_HIT_COORD, radius
        )
    }

    @Test
    fun withLayoutHint_ShouldCopyDataFromHint() {
        addMappedData(MappedDataAccessMock.variable().mapping(AES_WIDTH))

        createTooltipModels(
            geomTargetBuilder.withPathHitShape()
                .withLayoutHint(
                    AES_WIDTH, TooltipHint.verticalTooltip(
                        TARGET_HIT_COORD,
                        OBJECT_RADIUS,
                        markerColors = emptyList()
                    )
                )
                .build()
        )

        assertHint(
            VERTICAL,
            TARGET_HIT_COORD,
            OBJECT_RADIUS
        )
    }
}
