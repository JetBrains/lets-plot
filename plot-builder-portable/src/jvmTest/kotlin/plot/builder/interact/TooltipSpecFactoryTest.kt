/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.interact.TipLayoutHint
import jetbrains.datalore.plot.builder.interact.MappedDataAccessMock.Companion.variable
import kotlin.test.BeforeTest
import kotlin.test.Test

class TooltipSpecFactoryTest : jetbrains.datalore.plot.builder.interact.TooltipSpecTestHelper() {

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
        val colorMapping = addMappedData(variable().name("cyl").value("4").mapping(Aes.COLOR))

        createTooltipSpecs(geomTargetBuilder.withPathHitShape()
                .withLayoutHint(
                    AES_WIDTH, TipLayoutHint.verticalTooltip(
                        TARGET_HIT_COORD,
                        OBJECT_RADIUS,
                        FILL_COLOR
                    ))
                .build())

        assertLines(0, widthMapping.longTooltipText())
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
    fun whenFillColorProvided_ShouldUseItForTooltip() {
        addMappedData(variable().value("sedan").mapping(AES_WIDTH))

        createTooltipSpecs(geomTargetBuilder.withPathHitShape()
                .withFill(Color.RED)
                .build())

        assertFill(Color.RED)
    }

    @Test
    fun withLayoutHint_ShouldUseHintColor() {
        val widthMapping = addMappedData(variable().name("type").value("sedan").mapping(AES_WIDTH))

        val hintFill = Color.DARK_GREEN
        createTooltipSpecs(geomTargetBuilder.withPathHitShape()
                .withLayoutHint(
                    AES_WIDTH, TipLayoutHint.verticalTooltip(
                        TARGET_HIT_COORD,
                        OBJECT_RADIUS, hintFill))
                .withFill(Color.RED)
                .build())

        assertLines(0, widthMapping.longTooltipText())
        assertFill(hintFill)
    }
}
