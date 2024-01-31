/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.tooltip

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.tooltip.TipLayoutHint
import org.jetbrains.letsPlot.core.plot.builder.tooltip.MappedDataAccessMock.Companion.variable
import org.jetbrains.letsPlot.core.plot.builder.tooltip.data.MappingField
import kotlin.test.BeforeTest
import kotlin.test.Test

class TooltipSpecFactoryTest : TooltipSpecTestHelper() {

    @BeforeTest
    fun setUp() {
        init()
    }

    @Test
    fun whenAesFromTooltipListIsNotMapped_ShouldNotThrowException() {
        createTooltipSpecs(geomTargetBuilder.withPointHitShape(TARGET_HIT_COORD, 0.0).build())
    }

    @Test
    fun shouldNotDuplicateAesFromHintsToBigTooltip() {
        val widthMapping = addMappedData(variable().name("type").value("sedan").mapping(AES_WIDTH))
        val colorMapping =
            addMappedData(variable().name("cyl").value("4").mapping(Aes.COLOR))

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

        assertLines(0, widthMapping.shortTooltipText())
        assertLines(1, colorMapping.longTooltipText())
    }

    @Test
    fun shouldNotAddSemicolonIfLabelIsEmpty() {
        val widthMapping = addMappedData(variable().value("sedan").mapping(AES_WIDTH))

        buildTooltipSpecs()

        assertTooltipsCount(1)
        assertLines(0, widthMapping.shortTooltipText())
    }

    @Test
    fun checkIfTooltipIsSide() {
        val widthMapping = addMappedData(variable().name("type").value("sedan").mapping(AES_WIDTH))
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
        assertLines(listOf(widthMapping.shortTooltipText()), isSide = true)
    }

    @Test
    fun shouldNotAddSemicolonIfLineFormatterIsSet() {
//        val widthMapping = addMappedData(variable().name("type").value("sedan").mapping(AES_WIDTH))
        val widthMapping = addMappedData(variable().name("type").value("n/a").mapping(AES_WIDTH))
        // set line format -> short text will be used
        val widthAes = MappingField(
            AES_WIDTH,
            format = "{}"
        )
        createTooltipSpecWithValueSources(
            geomTargetBuilder.withPathHitShape()
                .withLayoutHint(
                    AES_WIDTH, TipLayoutHint.verticalTooltip(
                        TARGET_HIT_COORD,
                        OBJECT_RADIUS,
                        markerColors = emptyList()
                    )
                )
                .build(),
            valueSources = listOf(widthAes)
        )
        assertLines(listOf(widthMapping.shortTooltipText()), isSide = true)
    }
}
