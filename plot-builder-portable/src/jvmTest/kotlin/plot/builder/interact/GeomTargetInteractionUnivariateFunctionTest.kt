/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.builder.interact.TestUtil.assertNoTooltips
import jetbrains.datalore.plot.builder.interact.TestUtil.assertText
import jetbrains.datalore.plot.builder.interact.TestUtil.continuous
import jetbrains.datalore.plot.builder.interact.TestUtil.discrete
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
        return TestingTooltipSpecsBuilder.univariateFunctionBuilder(listOfNotNull(displayableAes))
    }

}
