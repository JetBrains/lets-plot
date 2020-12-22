/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact


import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.interact.GeomTarget
import jetbrains.datalore.plot.base.interact.GeomTargetLocator
import jetbrains.datalore.plot.base.interact.MappedDataAccess
import jetbrains.datalore.plot.base.interact.TipLayoutHint
import jetbrains.datalore.plot.base.interact.TipLayoutHint.Kind.VERTICAL_TOOLTIP
import jetbrains.datalore.plot.builder.interact.mockito.ReturnsNotNullValuesAnswer
import jetbrains.datalore.plot.builder.tooltip.TooltipSpecification
import org.mockito.Mockito.*


internal class TestingTooltipSpecsBuilder private constructor(
        private val contextualMappingProvider: ContextualMappingProvider
) {

    private val mappedDataAccessMock = MappedDataAccessMock()
    private val mockSettings = withSettings()
            .defaultAnswer(ReturnsNotNullValuesAnswer())

    fun build(): List<TooltipSpec> {
        val mappedDataAccess = buildMappedDataAccess()

        val contextualMapping = contextualMappingProvider.createContextualMapping(
            mappedDataAccess,
            DataFrame.Builder().build()
        )
        val factory = TooltipSpecFactory(contextualMapping, DoubleVector.ZERO)

        val tipLayoutHint = mock(TipLayoutHint::class.java, mockSettings)
        `when`(tipLayoutHint.kind).thenReturn(VERTICAL_TOOLTIP)
        `when`(tipLayoutHint.coord).thenReturn(DoubleVector.ZERO)
        `when`(tipLayoutHint.objectRadius).thenReturn(0.0)

        val geomTarget = mock(GeomTarget::class.java, mockSettings)
        `when`(geomTarget.tipLayoutHint).thenReturn(tipLayoutHint)

        return factory.create(geomTarget)
    }

    private fun buildMappedDataAccess(): MappedDataAccess {
        return mappedDataAccessMock.mappedDataAccess
    }

    fun <T> variable(mappedData: MappedDataAccessMock.Mapping<T>): TestingTooltipSpecsBuilder {
        mappedDataAccessMock.add(mappedData)
        return this
    }

    companion object {
        private val DISPLAYABLE_AES_LIST =
            toList(Aes.values())

        fun univariateFunctionBuilder(): TestingTooltipSpecsBuilder {
            return TestingTooltipSpecsBuilder(
                GeomInteractionBuilder(DISPLAYABLE_AES_LIST)
                    .univariateFunction(GeomTargetLocator.LookupStrategy.NEAREST)
                    .build()
            )
        }

        fun bivariateFunctionBuilder(): TestingTooltipSpecsBuilder {
            return TestingTooltipSpecsBuilder(
                GeomInteractionBuilder(DISPLAYABLE_AES_LIST)
                    .bivariateFunction(false)
                    .build()
            )
        }

        fun areaFunctionBuilder(): TestingTooltipSpecsBuilder {
            return TestingTooltipSpecsBuilder(
                GeomInteractionBuilder(DISPLAYABLE_AES_LIST)
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
