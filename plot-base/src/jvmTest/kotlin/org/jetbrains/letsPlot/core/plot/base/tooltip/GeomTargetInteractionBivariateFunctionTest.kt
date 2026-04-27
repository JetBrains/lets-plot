/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.tooltip

import org.jetbrains.letsPlot.core.plot.base.Aes
import kotlin.test.Test

class GeomTargetInteractionBivariateFunctionTest {

    @Test
    fun withNoMappings_ShouldNotAddTooltipText() {
        val targetTooltipModel = createBuilder()
            .build()

        TestUtil.assertNoTooltips(targetTooltipModel)
    }

    @Test
    fun whenXIsContinuous_ShouldAddTooltipText() {
        val mapping = TestUtil.continuous(Aes.X)
        val targetTooltipModel = createBuilder()
            .variable(mapping)
            .build()

        TestUtil.assertText(targetTooltipModel, mapping.shortTooltipText())
    }

    @Test
    fun whenXDiscrete_ShouldAddTooltipText() {
        val mapping = TestUtil.discrete(Aes.X)
        val targetTooltipModel = createBuilder()
            .variable(mapping)
            .build()

        TestUtil.assertText(targetTooltipModel, mapping.shortTooltipText())
    }

    @Test
    fun whenYIsContinuous_ShouldAddTooltipText() {
        val mapping = TestUtil.continuous(Aes.Y)
        val targetTooltipModel = createBuilder()
            .variable(mapping)
            .build()

        TestUtil.assertText(targetTooltipModel, mapping.shortTooltipText())
    }

    @Test
    fun whenYIsDiscrete_ShouldAddTooltipText() {
        val mapping = TestUtil.discrete(Aes.Y)
        val targetTooltipModel = createBuilder()
            .variable(mapping)
            .build()

        TestUtil.assertText(targetTooltipModel, mapping.shortTooltipText())
    }

    @Test
    fun whenWidthIsContinuous_ShouldAddTooltipText() {
        val mapping = TestUtil.continuous(Aes.WIDTH)
        val targetTooltipModel = createBuilder()
            .variable(mapping)
            .build()

        TestUtil.assertText(targetTooltipModel, mapping.longTooltipText())
    }

    @Test
    fun whenWidthIsDiscrete_ShouldAddTooltipText() {
        val mapping = TestUtil.discrete(Aes.WIDTH)
        val targetTooltipModel = createBuilder()
            .variable(mapping)
            .build()

        TestUtil.assertText(targetTooltipModel, mapping.longTooltipText())
    }

    private fun createBuilder(): TestingTooltipModelsBuilder {
        return TestingTooltipModelsBuilder.bivariateFunctionBuilder()
    }
}
