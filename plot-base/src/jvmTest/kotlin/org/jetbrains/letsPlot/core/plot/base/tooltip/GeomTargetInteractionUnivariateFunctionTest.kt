/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.tooltip

import org.jetbrains.letsPlot.core.plot.base.Aes
import kotlin.test.Test

class GeomTargetInteractionUnivariateFunctionTest {

    @Test
    fun withNoMappings_ShouldAddEmptyTooltipText() {
        val targetTooltipModel = createUnivariateFunctionBuilder(null)
            .build()

        TestUtil.assertNoTooltips(targetTooltipModel)
    }

    @Test
    fun whenPositionalXVar_ShouldAddTooltipText() {
        val mapping = TestUtil.continuous(Aes.X)
        val targetTooltipModel = createUnivariateFunctionBuilder(mapping.aes)
            .variable(mapping)
            .build()

        TestUtil.assertText(targetTooltipModel, mapping.shortTooltipText())
    }

    @Test
    fun whenPositionalDiscreteXVar_ShouldAddTooltipText() {
        val mapping = TestUtil.discrete(Aes.X)
        val targetTooltipModel = createUnivariateFunctionBuilder(mapping.aes)
            .variable(mapping)
            .build()

        TestUtil.assertText(targetTooltipModel, mapping.shortTooltipText())
    }

    @Test
    fun whenPositionalYVar_ShouldAddTooltipText() {
        val mapping = TestUtil.continuous(Aes.Y)
        val targetTooltipModel = createUnivariateFunctionBuilder(mapping.aes)
            .variable(mapping)
            .build()

        TestUtil.assertText(targetTooltipModel, mapping.shortTooltipText())
    }

    @Test
    fun whenPositionalDiscreteYVar_ShouldAddTooltipText() {
        val mapping = TestUtil.discrete(Aes.Y)
        val targetTooltipModel = createUnivariateFunctionBuilder(mapping.aes)
            .variable(mapping)
            .build()

        TestUtil.assertText(targetTooltipModel, mapping.shortTooltipText())
    }

    @Test
    fun whenWidthVar_ShouldAddTooltipText() {
        val mapping = TestUtil.continuous(Aes.WIDTH)
        val targetTooltipModel = createUnivariateFunctionBuilder(mapping.aes)
            .variable(mapping)
            .build()

        TestUtil.assertText(targetTooltipModel, mapping.longTooltipText())
    }

    @Test
    fun whenDiscreteWidthVar_ShouldAddTooltipText() {
        val mapping = TestUtil.discrete(Aes.WIDTH)
        val targetTooltipModel = createUnivariateFunctionBuilder(mapping.aes)
            .variable(mapping)
            .build()

        TestUtil.assertText(targetTooltipModel, mapping.longTooltipText())
    }

    private fun createUnivariateFunctionBuilder(displayableAes: Aes<*>?): TestingTooltipModelsBuilder {
        return TestingTooltipModelsBuilder.univariateFunctionBuilder(
            listOfNotNull(displayableAes)
        )
    }

}
