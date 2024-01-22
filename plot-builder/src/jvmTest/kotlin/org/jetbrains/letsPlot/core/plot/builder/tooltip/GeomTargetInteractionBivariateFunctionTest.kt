/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.tooltip

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.builder.tooltip.TestUtil.assertNoTooltips
import org.jetbrains.letsPlot.core.plot.builder.tooltip.TestUtil.assertText
import org.jetbrains.letsPlot.core.plot.builder.tooltip.TestUtil.continuous
import org.jetbrains.letsPlot.core.plot.builder.tooltip.TestUtil.discrete
import kotlin.test.Test

class GeomTargetInteractionBivariateFunctionTest {

    @Test
    fun withNoMappings_ShouldNotAddTooltipText() {
        val targetTooltipSpec = createBuilder()
            .build()

        assertNoTooltips(targetTooltipSpec)
    }

    @Test
    fun whenXIsContinuous_ShouldAddTooltipText() {
        val mapping = continuous(Aes.X)
        val targetTooltipSpec = createBuilder()
            .variable(mapping)
            .build()

        assertText(targetTooltipSpec, mapping.shortTooltipText())
    }

    @Test
    fun whenXDiscrete_ShouldAddTooltipText() {
        val mapping = discrete(Aes.X)
        val targetTooltipSpec = createBuilder()
            .variable(mapping)
            .build()

        assertText(targetTooltipSpec, mapping.shortTooltipText())
    }

    @Test
    fun whenYIsContinuous_ShouldAddTooltipText() {
        val mapping = continuous(Aes.Y)
        val targetTooltipSpec = createBuilder()
            .variable(mapping)
            .build()

        assertText(targetTooltipSpec, mapping.shortTooltipText())
    }

    @Test
    fun whenYIsDiscrete_ShouldAddTooltipText() {
        val mapping = discrete(Aes.Y)
        val targetTooltipSpec = createBuilder()
            .variable(mapping)
            .build()

        assertText(targetTooltipSpec, mapping.shortTooltipText())
    }

    @Test
    fun whenWidthIsContinuous_ShouldAddTooltipText() {
        val mapping = continuous(Aes.WIDTH)
        val targetTooltipSpec = createBuilder()
            .variable(mapping)
            .build()

        assertText(targetTooltipSpec, mapping.longTooltipText())
    }

    @Test
    fun whenWidthIsDiscrete_ShouldAddTooltipText() {
        val mapping = discrete(Aes.WIDTH)
        val targetTooltipSpec = createBuilder()
            .variable(mapping)
            .build()

        assertText(targetTooltipSpec, mapping.longTooltipText())
    }

    private fun createBuilder(): TestingTooltipSpecsBuilder {
        return TestingTooltipSpecsBuilder.bivariateFunctionBuilder()
    }
}
