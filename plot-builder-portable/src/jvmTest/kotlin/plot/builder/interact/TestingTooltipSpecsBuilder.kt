/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact


import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.interact.GeomTarget
import org.jetbrains.letsPlot.core.plot.base.interact.GeomTargetLocator
import org.jetbrains.letsPlot.core.plot.base.interact.MappedDataAccess
import org.jetbrains.letsPlot.core.plot.base.interact.TipLayoutHint
import org.jetbrains.letsPlot.core.plot.base.interact.TipLayoutHint.Kind.VERTICAL_TOOLTIP
import jetbrains.datalore.plot.builder.interact.TestUtil.axisTheme
import jetbrains.datalore.plot.builder.interact.mockito.ReturnsNotNullValuesAnswer
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
            toList(org.jetbrains.letsPlot.core.plot.base.Aes.values())

        fun univariateFunctionBuilder(displayableAesList: List<org.jetbrains.letsPlot.core.plot.base.Aes<*>> = DISPLAYABLE_AES_LIST): TestingTooltipSpecsBuilder {
            return TestingTooltipSpecsBuilder(
                GeomInteractionBuilder.DemoAndTest(displayableAesList)
                    .xUnivariateFunction(GeomTargetLocator.LookupStrategy.NEAREST)
                    .build()
            )
        }

        fun bivariateFunctionBuilder(displayableAesList: List<org.jetbrains.letsPlot.core.plot.base.Aes<*>> = DISPLAYABLE_AES_LIST): TestingTooltipSpecsBuilder {
            return TestingTooltipSpecsBuilder(
                GeomInteractionBuilder.DemoAndTest(displayableAesList)
                    .bivariateFunction(false)
                    .build()
            )
        }

        fun areaFunctionBuilder(displayableAesList: List<org.jetbrains.letsPlot.core.plot.base.Aes<*>> = DISPLAYABLE_AES_LIST): TestingTooltipSpecsBuilder {
            return TestingTooltipSpecsBuilder(
                GeomInteractionBuilder.DemoAndTest(displayableAesList)
                    .bivariateFunction(true)
                    .build()
            )
        }

        private fun toList(aes: Iterable<org.jetbrains.letsPlot.core.plot.base.Aes<*>>): List<org.jetbrains.letsPlot.core.plot.base.Aes<*>> {
            val target = ArrayList<org.jetbrains.letsPlot.core.plot.base.Aes<*>>()
            aes.forEach { target.add(it) }

            return target
        }
    }

}
