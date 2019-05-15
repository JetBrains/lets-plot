package jetbrains.datalore.visualization.plot.gog.config.event3


import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.plot.gog.core.event.MappedDataAccess
import jetbrains.datalore.visualization.plot.gog.core.event3.GeomTarget
import jetbrains.datalore.visualization.plot.gog.core.event3.GeomTargetLocator
import jetbrains.datalore.visualization.plot.gog.core.event3.TipLayoutHint
import jetbrains.datalore.visualization.plot.gog.core.event3.TipLayoutHint.Kind.VERTICAL_TOOLTIP
import jetbrains.datalore.visualization.plot.gog.core.render.Aes
import jetbrains.datalore.visualization.plot.gog.mockito.ReturnsNotNullValuesAnswer
import jetbrains.datalore.visualization.plot.gog.plot.event3.ContextualMappingProvider
import jetbrains.datalore.visualization.plot.gog.plot.event3.MappedDataAccessMock
import jetbrains.datalore.visualization.plot.gog.plot.event3.MappedDataAccessMock.Mapping
import jetbrains.datalore.visualization.plot.gog.plot.event3.TargetTooltipSpec
import org.mockito.Mockito.*


internal class TargetTooltipSpecBuilder private constructor(
        private val contextualMappingProvider: ContextualMappingProvider) {

    private val mappedDataAccessMock = MappedDataAccessMock()
    private val mockSettings = withSettings()
            .defaultAnswer(ReturnsNotNullValuesAnswer())

    fun build(): TargetTooltipSpec {
        val mappedDataAccess = buildMappedDataAccess()

        val contextualMapping = contextualMappingProvider.createContextualMapping(mappedDataAccess)
        val factory = TooltipSpecFactory(contextualMapping, DoubleVector.ZERO)

        val tipLayoutHint = mock(TipLayoutHint::class.java, mockSettings)
        `when`(tipLayoutHint.kind).thenReturn(VERTICAL_TOOLTIP)
        `when`(tipLayoutHint.coord).thenReturn(DoubleVector.ZERO)
        `when`(tipLayoutHint.objectRadius).thenReturn(0.0)

        val geomTarget = mock(GeomTarget::class.java, mockSettings)
        `when`(geomTarget.tipLayoutHint).thenReturn(tipLayoutHint)

        return TargetTooltipSpec(factory.create(geomTarget))
    }

    private fun buildMappedDataAccess(): MappedDataAccess {
        return mappedDataAccessMock.mappedDataAccess
    }

    fun <T> variable(mappedData: Mapping<T>): TargetTooltipSpecBuilder {
        mappedDataAccessMock.add(mappedData)
        return this
    }

    companion object {
        private val DISPLAYABLE_AES_LIST = toList(Aes.values())

        fun univariateFunctionBuilder(): TargetTooltipSpecBuilder {
            return TargetTooltipSpecBuilder(
                    GeomInteractionBuilder(DISPLAYABLE_AES_LIST)
                            .univariateFunction(GeomTargetLocator.LookupStrategy.NEAREST)
                            .build()
            )
        }

        fun bivariateFunctionBuilder(): TargetTooltipSpecBuilder {
            return TargetTooltipSpecBuilder(
                    GeomInteractionBuilder(DISPLAYABLE_AES_LIST)
                            .bivariateFunction(false)
                            .build()
            )
        }

        fun areaFunctionBuilder(): TargetTooltipSpecBuilder {
            return TargetTooltipSpecBuilder(
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
