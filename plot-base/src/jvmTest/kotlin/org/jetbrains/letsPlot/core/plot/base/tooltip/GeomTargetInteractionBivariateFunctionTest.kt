/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.tooltip

import org.jetbrains.letsPlot.core.plot.base.Aes
import kotlin.test.Test

class GeomTargetInteractionBivariateFunctionTest {

    @Test
    fun withNoMappings_ShouldNotAddTooltipText() {
        val targetTooltipSpec = createBuilder()
            .build()

        TestUtil.assertNoTooltips(targetTooltipSpec)
    }

    @Test
    fun whenXIsContinuous_ShouldAddTooltipText() {
        val mapping = TestUtil.continuous(Aes.X)
        val targetTooltipSpec = createBuilder()
            .variable(mapping)
            .build()

        TestUtil.assertText(targetTooltipSpec, mapping.shortTooltipText())
    }

    @Test
    fun whenXDiscrete_ShouldAddTooltipText() {
        val mapping = TestUtil.discrete(Aes.X)
        val targetTooltipSpec = createBuilder()
            .variable(mapping)
            .build()

        TestUtil.assertText(targetTooltipSpec, mapping.shortTooltipText())
    }

    @Test
    fun whenYIsContinuous_ShouldAddTooltipText() {
        val mapping = TestUtil.continuous(Aes.Y)
        val targetTooltipSpec = createBuilder()
            .variable(mapping)
            .build()

        TestUtil.assertText(targetTooltipSpec, mapping.shortTooltipText())
    }

    @Test
    fun whenYIsDiscrete_ShouldAddTooltipText() {
        val mapping = TestUtil.discrete(Aes.Y)
        val targetTooltipSpec = createBuilder()
            .variable(mapping)
            .build()

        TestUtil.assertText(targetTooltipSpec, mapping.shortTooltipText())
    }

    @Test
    fun whenWidthIsContinuous_ShouldAddTooltipText() {
        val mapping = TestUtil.continuous(Aes.WIDTH)
        val targetTooltipSpec = createBuilder()
            .variable(mapping)
            .build()

        TestUtil.assertText(targetTooltipSpec, mapping.longTooltipText())
    }

    @Test
    fun whenWidthIsDiscrete_ShouldAddTooltipText() {
        val mapping = TestUtil.discrete(Aes.WIDTH)
        val targetTooltipSpec = createBuilder()
            .variable(mapping)
            .build()

        TestUtil.assertText(targetTooltipSpec, mapping.longTooltipText())
    }

    private fun createBuilder(): TestingTooltipSpecsBuilder {
        return TestingTooltipSpecsBuilder.bivariateFunctionBuilder()
    }
}
