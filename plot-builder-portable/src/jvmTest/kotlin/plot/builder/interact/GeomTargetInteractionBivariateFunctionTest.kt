/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact

import org.jetbrains.letsPlot.core.plot.base.Aes
import jetbrains.datalore.plot.builder.interact.TestUtil.assertNoTooltips
import jetbrains.datalore.plot.builder.interact.TestUtil.assertText
import jetbrains.datalore.plot.builder.interact.TestUtil.continuous
import jetbrains.datalore.plot.builder.interact.TestUtil.discrete
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
        val mapping = continuous(org.jetbrains.letsPlot.core.plot.base.Aes.X)
        val targetTooltipSpec = createBuilder()
                .variable(mapping)
                .build()

        assertText(targetTooltipSpec, mapping.shortTooltipText())
    }

    @Test
    fun whenXDiscrete_ShouldAddTooltipText() {
        val mapping = discrete(org.jetbrains.letsPlot.core.plot.base.Aes.X)
        val targetTooltipSpec = createBuilder()
            .variable(mapping)
            .build()

        assertText(targetTooltipSpec, mapping.shortTooltipText())
    }

    @Test
    fun whenYIsContinuous_ShouldAddTooltipText() {
        val mapping = continuous(org.jetbrains.letsPlot.core.plot.base.Aes.Y)
        val targetTooltipSpec = createBuilder()
                .variable(mapping)
                .build()

        assertText(targetTooltipSpec, mapping.shortTooltipText())
    }

    @Test
    fun whenYIsDiscrete_ShouldAddTooltipText() {
        val mapping = discrete(org.jetbrains.letsPlot.core.plot.base.Aes.Y)
        val targetTooltipSpec = createBuilder()
            .variable(mapping)
            .build()

        assertText(targetTooltipSpec, mapping.shortTooltipText())
    }

    @Test
    fun whenWidthIsContinuous_ShouldAddTooltipText() {
        val mapping = continuous(org.jetbrains.letsPlot.core.plot.base.Aes.WIDTH)
        val targetTooltipSpec = createBuilder()
                .variable(mapping)
                .build()

        assertText(targetTooltipSpec, mapping.longTooltipText())
    }

    @Test
    fun whenWidthIsDiscrete_ShouldAddTooltipText() {
        val mapping = discrete(org.jetbrains.letsPlot.core.plot.base.Aes.WIDTH)
        val targetTooltipSpec = createBuilder()
                .variable(mapping)
                .build()

        assertText(targetTooltipSpec, mapping.longTooltipText())
    }

    private fun createBuilder(): TestingTooltipSpecsBuilder {
        return TestingTooltipSpecsBuilder.bivariateFunctionBuilder()
    }
}
