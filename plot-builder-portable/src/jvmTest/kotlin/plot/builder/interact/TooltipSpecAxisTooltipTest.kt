/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.interact.TipLayoutHint
import jetbrains.datalore.plot.base.interact.TipLayoutHint.Kind.X_AXIS_TOOLTIP
import jetbrains.datalore.plot.builder.interact.MappedDataAccessMock.Companion.variable
import jetbrains.datalore.plot.builder.presentation.Defaults.Common.Tooltip.AXIS_TOOLTIP_COLOR
import kotlin.test.BeforeTest
import kotlin.test.Test

class TooltipSpecAxisTooltipTest : jetbrains.datalore.plot.builder.interact.TooltipSpecTestHelper() {

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
    fun shouldNotAddLabel_WhenMappedToYAxisVar() {
        val v = variable().name("var_for_y").value("sedan")

        val fillMapping = addMappedData(v.mapping(Aes.FILL))
        val yMapping = addMappedData(v.mapping(Aes.Y))

        createTooltipSpecs(
            geomTargetBuilder.withPathHitShape()
                .withLayoutHint(
                    Aes.FILL, TipLayoutHint.verticalTooltip(
                        TARGET_HIT_COORD,
                        OBJECT_RADIUS,
                        FILL_COLOR
                    )
                )
                .build()
        )

        assertLines(0, fillMapping.shortTooltipText())
        assertLines(1, yMapping.shortTooltipText())
    }
}
