/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.tooltip


import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.NullPlotContext
import org.jetbrains.letsPlot.core.plot.base.tooltip.TooltipHint.Placement.VERTICAL
import org.jetbrains.letsPlot.core.plot.base.tooltip.conf.GeomInteractionBuilder
import org.jetbrains.letsPlot.core.plot.base.tooltip.loc.createTooltipModels
import org.jetbrains.letsPlot.core.plot.base.tooltip.mockito.ReturnsNotNullValuesAnswer
import org.jetbrains.letsPlot.core.plot.base.tooltip.text.MappedDataAccess
import org.mockito.Mockito.*


internal class TestingTooltipModelsBuilder private constructor(
    private val contextualMappingProvider: ContextualMappingProvider
) {

    private val mappedDataAccessMock = MappedDataAccessMock()
    private val mockSettings = withSettings()
        .defaultAnswer(ReturnsNotNullValuesAnswer())

    private val plotContext = NullPlotContext

    fun build(): List<TooltipModel> {
        val mappedDataAccess = buildMappedDataAccess()

        val contextualMapping = contextualMappingProvider.createContextualMapping(
            mappedDataAccess,
            DataFrame.Builder().build()
        )
        val tooltipHint = mock(TooltipHint::class.java, mockSettings)
        `when`(tooltipHint.placement).thenReturn(VERTICAL)
        `when`(tooltipHint.coord).thenReturn(DoubleVector.ZERO)
        `when`(tooltipHint.objectRadius).thenReturn(0.0)

        val geomTarget = mock(GeomTarget::class.java, mockSettings)
        `when`(geomTarget.tooltipHint).thenReturn(tooltipHint)

        return createTooltipModels(
            geomTarget = geomTarget,
            contextualMapping = contextualMapping,
            axisOrigin = DoubleVector.ZERO,
            flippedAxis = false,
            xAxisTheme = TestUtil.axisTheme,
            yAxisTheme = TestUtil.axisTheme,
            ctx = plotContext
        )
    }

    private fun buildMappedDataAccess(): MappedDataAccess {
        return mappedDataAccessMock.mappedDataAccess
    }

    fun <T> variable(mappedData: MappedDataAccessMock.Mapping<T>): TestingTooltipModelsBuilder {
        mappedDataAccessMock.add(mappedData)
        return this
    }

    companion object {
        private val DISPLAYABLE_AES_LIST =
            toList(Aes.values())

        fun univariateFunctionBuilder(displayableAesList: List<Aes<*>> = DISPLAYABLE_AES_LIST): TestingTooltipModelsBuilder {
            return TestingTooltipModelsBuilder(
                GeomInteractionBuilder.DemoAndTest(displayableAesList)
                    .xUnivariateFunction(GeomTargetLocator.LookupStrategy.NEAREST)
                    .build()
            )
        }

        fun bivariateFunctionBuilder(displayableAesList: List<Aes<*>> = DISPLAYABLE_AES_LIST): TestingTooltipModelsBuilder {
            return TestingTooltipModelsBuilder(
                GeomInteractionBuilder.DemoAndTest(displayableAesList)
                    .bivariateFunction(false)
                    .build()
            )
        }

        fun areaFunctionBuilder(displayableAesList: List<Aes<*>> = DISPLAYABLE_AES_LIST): TestingTooltipModelsBuilder {
            return TestingTooltipModelsBuilder(
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
