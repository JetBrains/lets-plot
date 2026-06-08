/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.tooltip


import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.tooltip.TooltipHint.Placement.VERTICAL
import org.jetbrains.letsPlot.core.plot.base.tooltip.loc.LocatedTargetsPicker
import org.jetbrains.letsPlot.core.plot.base.tooltip.mockito.ReturnsNotNullValuesAnswer
import org.jetbrains.letsPlot.core.plot.base.tooltip.text.MappedDataAccess
import org.mockito.Mockito.*


internal class TestingTooltipModelsBuilder private constructor(
    private val contextualMappingProviderFactory: (Collection<MappedDataAccessMock.Mapping<*>>) -> ContextualMappingProvider
) {

    private val mappedDataAccessMock = MappedDataAccessMock()
    private val mockSettings = withSettings()
        .defaultAnswer(ReturnsNotNullValuesAnswer())

    private val plotContext = NullPlotContext

    fun build(): List<TooltipModel> {
        val mappedDataAccess = buildMappedDataAccess()

        val contextualMapping = contextualMappingProviderFactory(mappedDataAccessMock.getMappings()).createContextualMapping(
            mappedDataAccess,
            DataFrame.Builder().build()
        )
        val tooltipHint = mock(TooltipHint::class.java, mockSettings)
        `when`(tooltipHint.placement).thenReturn(VERTICAL)
        `when`(tooltipHint.coord).thenReturn(DoubleVector.ZERO)
        `when`(tooltipHint.objectRadius).thenReturn(0.0)
        `when`(tooltipHint.marker).thenReturn(TooltipMarker.NONE)

        val geomTarget = mock(GeomTarget::class.java, mockSettings)
        `when`(geomTarget.tooltipHint).thenReturn(tooltipHint)

        return LocatedTargetsPicker(
            flippedAxis = false,
            cursorCoord = DoubleVector.ZERO,
            axisOrigin = DoubleVector.ZERO,
            xAxisTheme = TestUtil.axisTheme,
            yAxisTheme = TestUtil.axisTheme,
            ctx = plotContext
        ).chooseTooltipModels(listOf(geomTarget), contextualMapping)
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
            return TestingTooltipModelsBuilder { mappings ->
                GeomInteractionTestingFactory.createBuilder(
                    geomKind = GeomKind.RIBBON,
                    statKind = StatKind.IDENTITY,
                    renderedAes = displayableAesList,
                    mappings = mappings
                ).build()
            }
        }

        fun bivariateFunctionBuilder(displayableAesList: List<Aes<*>> = DISPLAYABLE_AES_LIST): TestingTooltipModelsBuilder {
            return TestingTooltipModelsBuilder { mappings ->
                GeomInteractionTestingFactory.createBuilder(
                    geomKind = GeomKind.POINT,
                    statKind = StatKind.IDENTITY,
                    renderedAes = displayableAesList,
                    mappings = mappings
                ).build()
            }
        }

        fun areaFunctionBuilder(displayableAesList: List<Aes<*>> = DISPLAYABLE_AES_LIST): TestingTooltipModelsBuilder {
            return TestingTooltipModelsBuilder { mappings ->
                GeomInteractionTestingFactory.createBuilder(
                    geomKind = GeomKind.RECT,
                    statKind = StatKind.IDENTITY,
                    renderedAes = displayableAesList,
                    mappings = mappings
                ).build()
            }
        }

        private fun toList(aes: Iterable<Aes<*>>): List<Aes<*>> {
            val target = ArrayList<Aes<*>>()
            aes.forEach { target.add(it) }

            return target
        }
    }

}
