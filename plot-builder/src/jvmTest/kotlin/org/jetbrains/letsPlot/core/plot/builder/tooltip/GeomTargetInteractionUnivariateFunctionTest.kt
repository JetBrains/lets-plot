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

class GeomTargetInteractionUnivariateFunctionTest {

    @Test
    fun withNoMappings_ShouldAddEmptyTooltipText() {
        val targetTooltipSpec = createUnivariateFunctionBuilder(null)
            .build()

        assertNoTooltips(targetTooltipSpec)
    }

    @Test
    fun whenPositionalXVar_ShouldAddTooltipText() {
        val mapping = continuous(Aes.X)
        val targetTooltipSpec = createUnivariateFunctionBuilder(mapping.aes)
            .variable(mapping)
            .build()

        assertText(targetTooltipSpec, mapping.shortTooltipText())
    }

    @Test
    fun whenPositionalDiscreteXVar_ShouldAddTooltipText() {
        val mapping = discrete(Aes.X)
        val targetTooltipSpec = createUnivariateFunctionBuilder(mapping.aes)
            .variable(mapping)
            .build()

        assertText(targetTooltipSpec, mapping.shortTooltipText())
    }

    @Test
    fun whenPositionalYVar_ShouldAddTooltipText() {
        val mapping = continuous(Aes.Y)
        val targetTooltipSpec = createUnivariateFunctionBuilder(mapping.aes)
            .variable(mapping)
            .build()

        assertText(targetTooltipSpec, mapping.shortTooltipText())
    }

    @Test
    fun whenPositionalDiscreteYVar_ShouldAddTooltipText() {
        val mapping = discrete(Aes.Y)
        val targetTooltipSpec = createUnivariateFunctionBuilder(mapping.aes)
            .variable(mapping)
            .build()

        assertText(targetTooltipSpec, mapping.shortTooltipText())
    }

    @Test
    fun whenWidthVar_ShouldAddTooltipText() {
        val mapping = continuous(Aes.WIDTH)
        val targetTooltipSpec = createUnivariateFunctionBuilder(mapping.aes)
            .variable(mapping)
            .build()

        assertText(targetTooltipSpec, mapping.longTooltipText())
    }

    @Test
    fun whenDiscreteWidthVar_ShouldAddTooltipText() {
        val mapping = discrete(Aes.WIDTH)
        val targetTooltipSpec = createUnivariateFunctionBuilder(mapping.aes)
            .variable(mapping)
            .build()

        assertText(targetTooltipSpec, mapping.longTooltipText())
    }

    private fun createUnivariateFunctionBuilder(displayableAes: Aes<*>?): TestingTooltipSpecsBuilder {
        return TestingTooltipSpecsBuilder.univariateFunctionBuilder(
            listOfNotNull(displayableAes)
        )
    }

}
