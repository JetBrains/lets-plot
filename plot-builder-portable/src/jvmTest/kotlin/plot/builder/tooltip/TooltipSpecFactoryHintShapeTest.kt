/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.tooltip

import jetbrains.datalore.plot.builder.tooltip.MappedDataAccessMock.Companion.variable
import jetbrains.datalore.plot.builder.tooltip.TestUtil.rect
import jetbrains.datalore.plot.builder.tooltip.TestUtil.size
import org.jetbrains.letsPlot.core.plot.base.tooltip.TipLayoutHint
import org.jetbrains.letsPlot.core.plot.base.tooltip.TipLayoutHint.Kind.*
import kotlin.test.BeforeTest
import kotlin.test.Test

class TooltipSpecFactoryHintShapeTest : TooltipSpecTestHelper() {

    @BeforeTest
    fun setUp() {
        init()

        // Add mapping otherwise hint will not be created
        addMappedData(variable().mapping(AES_WIDTH))
    }

    @Test
    fun withPointHitShape_ShouldAddHintAroundPoint() {
        createTooltipSpecs(geomTargetBuilder.withPointHitShape(TARGET_HIT_COORD, 0.0).build())

        assertHint(
            VERTICAL_TOOLTIP,
            TARGET_HIT_COORD,
            DEFAULT_OBJECT_RADIUS
        )
    }

    @Test
    fun withPathHitShape_ShouldAddHintMiddleAtY() {
        createTooltipSpecs(geomTargetBuilder.withPathHitShape().build())

        assertHint(
            HORIZONTAL_TOOLTIP,
            TARGET_HIT_COORD,
            DEFAULT_OBJECT_RADIUS
        )
    }

    @Test
    fun withPolygonHitShape_ShouldAddHintUnderCursor() {
        createTooltipSpecs(geomTargetBuilder.withPolygonHitShape(CURSOR_COORD).build())

        assertHint(
            CURSOR_TOOLTIP,
            CURSOR_COORD,
            DEFAULT_OBJECT_RADIUS
        )
    }

    @Test
    fun withRectHitShape_ShouldAddHintMiddleAtY() {
        val dim = size(10.0, 12.0)

        createTooltipSpecs(geomTargetBuilder.withRectHitShape(rect(TARGET_HIT_COORD, dim)).build())

        val radius = dim.x / 2
        assertHint(
            HORIZONTAL_TOOLTIP,
            TARGET_HIT_COORD, radius
        )
    }

    @Test
    fun withLayoutHint_ShouldCopyDataFromHint() {
        addMappedData(variable().mapping(AES_WIDTH))

        createTooltipSpecs(
            geomTargetBuilder.withPathHitShape()
                .withLayoutHint(
                    AES_WIDTH, TipLayoutHint.verticalTooltip(
                        TARGET_HIT_COORD,
                        OBJECT_RADIUS,
                        markerColors = emptyList()
                    )
                )
                .build()
        )

        assertHint(
            VERTICAL_TOOLTIP,
            TARGET_HIT_COORD,
            OBJECT_RADIUS
        )
    }
}
