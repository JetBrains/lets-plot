/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.tooltip


import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.tooltip.*
import org.jetbrains.letsPlot.core.plot.base.tooltip.TipLayoutHint.Kind.VERTICAL_TOOLTIP
import org.jetbrains.letsPlot.core.plot.builder.tooltip.TestUtil.axisTheme
import org.jetbrains.letsPlot.core.plot.builder.tooltip.conf.GeomInteractionBuilder
import org.jetbrains.letsPlot.core.plot.builder.tooltip.mockito.ReturnsNotNullValuesAnswer
import org.jetbrains.letsPlot.core.plot.builder.tooltip.spec.TooltipSpec
import org.jetbrains.letsPlot.core.plot.builder.tooltip.spec.TooltipSpecFactory
import org.mockito.Mockito.*


internal class TestingTooltipSpecsBuilder private constructor(
    private val contextualMappingProvider: ContextualMappingProvider
) {

    private val mappedDataAccessMock = MappedDataAccessMock()
    private val mockSettings = withSettings()
        .defaultAnswer(ReturnsNotNullValuesAnswer())

    private val plotContext = TestingPlotContextWithTooltipFormatters()

    fun build(): List<TooltipSpec> {
        val mappedDataAccess = buildMappedDataAccess()

        val contextualMapping = contextualMappingProvider.createContextualMapping(
            mappedDataAccess,
            DataFrame.Builder().build()
        )
        val factory =
            TooltipSpecFactory(contextualMapping, DoubleVector.ZERO, flippedAxis = false, axisTheme, axisTheme)

        val tipLayoutHint = mock(TipLayoutHint::class.java, mockSettings)
        `when`(tipLayoutHint.kind).thenReturn(VERTICAL_TOOLTIP)
        `when`(tipLayoutHint.coord).thenReturn(DoubleVector.ZERO)
        `when`(tipLayoutHint.objectRadius).thenReturn(0.0)

        val geomTarget = mock(GeomTarget::class.java, mockSettings)
        `when`(geomTarget.tipLayoutHint).thenReturn(tipLayoutHint)

        return factory.create(geomTarget, plotContext)
    }

    private fun buildMappedDataAccess(): MappedDataAccess {
        return mappedDataAccessMock.mappedDataAccess
    }

    fun <T> variable(mappedData: MappedDataAccessMock.Mapping<T>): TestingTooltipSpecsBuilder {
        mappedDataAccessMock.add(mappedData)
        plotContext.addMappedData(mappedData)
        return this
    }

    companion object {
        private val DISPLAYABLE_AES_LIST =
            toList(Aes.values())

        fun univariateFunctionBuilder(displayableAesList: List<Aes<*>> = DISPLAYABLE_AES_LIST): TestingTooltipSpecsBuilder {
            return TestingTooltipSpecsBuilder(
                GeomInteractionBuilder.DemoAndTest(displayableAesList)
                    .xUnivariateFunction(GeomTargetLocator.LookupStrategy.NEAREST)
                    .build()
            )
        }

        fun bivariateFunctionBuilder(displayableAesList: List<Aes<*>> = DISPLAYABLE_AES_LIST): TestingTooltipSpecsBuilder {
            return TestingTooltipSpecsBuilder(
                GeomInteractionBuilder.DemoAndTest(displayableAesList)
                    .bivariateFunction(false)
                    .build()
            )
        }

        fun areaFunctionBuilder(displayableAesList: List<Aes<*>> = DISPLAYABLE_AES_LIST): TestingTooltipSpecsBuilder {
            return TestingTooltipSpecsBuilder(
                GeomInteractionBuilder.DemoAndTest(displayableAesList)
                    .bivariateFunction(true)
                    .build()
            )
        }

        private fun toList(aes: Iterable<Aes<*>>): List<Aes<*>> {
            val target = ArrayList<Aes<*>>()
            aes.forEach { target.add(it) }

            return target
        }
    }

}
