/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.tooltip

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.tooltip.TipLayoutHint
import org.jetbrains.letsPlot.core.plot.base.tooltip.TipLayoutHint.Kind.X_AXIS_TOOLTIP
import org.jetbrains.letsPlot.core.plot.builder.presentation.Defaults.Common.Tooltip.AXIS_TOOLTIP_COLOR
import org.jetbrains.letsPlot.core.plot.builder.tooltip.MappedDataAccessMock.Companion.variable
import kotlin.test.BeforeTest
import kotlin.test.Test

class TooltipSpecAxisTooltipTest : TooltipSpecTestHelper() {

    @BeforeTest
    fun setUp() {
        init()
        setAxisTooltipEnabled(true)
    }

    @Test
    fun whenXIsNotMapped_ShouldNotThrowException() {
        createTooltipSpecs(geomTargetBuilder.withPointHitShape(TARGET_HIT_COORD, 0.0).build())
    }

    @Test
    fun shouldNotAddLabel_WhenMappedToYAxisVar() {
        val v = variable().name("var_for_y").value("sedan")

        val fillMapping = addMappedData(v.mapping(Aes.FILL))
        val yMapping = addMappedData(v.mapping(Aes.Y))

        createTooltipSpecs(
            geomTargetBuilder.withPathHitShape()
                .withLayoutHint(
                    Aes.FILL,
                    TipLayoutHint.verticalTooltip(
                        TARGET_HIT_COORD,
                        OBJECT_RADIUS,
                        markerColors = emptyList()
                    )
                )
                .build()
        )

        assertLines(0, fillMapping.shortTooltipText())
        assertLines(1, yMapping.shortTooltipText())
    }

    @Test
    fun whenXIsMapped_AndAxisTooltipEnabled_ShouldAddTooltipSpec() {
        val variable = variable().name("some label").value("some value").isContinuous(true)
        val xMapping = addMappedData(variable.mapping(Aes.X))

        buildTooltipSpecs()

        assertHint(
            expectedHintKind = X_AXIS_TOOLTIP,
            expectedHintCoord = TARGET_X_AXIS_COORD,
            expectedObjectRadius = 1.5
        )
        assertFill(AXIS_TOOLTIP_COLOR)
        assertLines(0, xMapping.shortTooltipText())
    }


    @Test
    fun shouldNotAddLabel_When_MappedToYAxisVar_And_OneLineTooltip() {
        val v = variable().name("var_for_y").value("sedan")
        val yMapping = addMappedData(v.mapping(Aes.Y))

        buildTooltipSpecs()
        assertLines(0, yMapping.shortTooltipText())
    }

    @Test
    fun multilineTooltip_shouldAddLabels() {
        val v = variable().name("var_for_y").value("sedan")
        val fillMapping = addMappedData(v.mapping(Aes.FILL))
        val yMapping = addMappedData(v.mapping(Aes.Y))

        buildTooltipSpecs()
        assertLines(0, fillMapping.longTooltipText(), yMapping.longTooltipText())
    }
}
