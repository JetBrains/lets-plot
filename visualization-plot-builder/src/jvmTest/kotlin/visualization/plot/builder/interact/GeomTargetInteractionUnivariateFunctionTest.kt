package jetbrains.datalore.visualization.plot.builder.interact

import jetbrains.datalore.visualization.plot.base.Aes
import jetbrains.datalore.visualization.plot.builder.interact.TestUtil.assertNoTooltips
import jetbrains.datalore.visualization.plot.builder.interact.TestUtil.assertText
import jetbrains.datalore.visualization.plot.builder.interact.TestUtil.continuous
import jetbrains.datalore.visualization.plot.builder.interact.TestUtil.discrete
import kotlin.test.Test

class GeomTargetInteractionUnivariateFunctionTest {

    @Test
    fun withNoMappings_ShouldAddEmptyTooltipText() {
        val targetTooltipSpec = createUnivariateFunctionBuilder()
                .build()

        assertNoTooltips(targetTooltipSpec)
    }

    @Test
    fun whenPositionalXVar_ShouldAddEmptyTooltipText() {
        val mapping = continuous(Aes.X)
        val targetTooltipSpec = createUnivariateFunctionBuilder()
                .variable(mapping)
                .build()

        assertText(targetTooltipSpec, mapping.shortTooltipText())
    }

    @Test
    fun whenPositionalNonNumericXVar_ShouldAddEmptyTooltipText() {
        val targetTooltipSpec = createUnivariateFunctionBuilder()
                .variable(discrete(Aes.X))
                .build()

        assertNoTooltips(targetTooltipSpec)
    }

    @Test
    fun whenPositionalYVar_ShouldAddTooltipText() {
        val mapping = continuous(Aes.Y)
        val targetTooltipSpec = createUnivariateFunctionBuilder()
                .variable(mapping)
                .build()

        assertText(targetTooltipSpec, mapping.shortTooltipText())
    }

    @Test
    fun whenPositionalNonNumericYVar_ShouldAddTooltipText() {
        val mapping = discrete(Aes.Y)
        val targetTooltipSpec = createUnivariateFunctionBuilder()
                .variable(mapping)
                .build()

        assertText(targetTooltipSpec, mapping.shortTooltipText())
    }

    @Test
    fun whenWidthVar_ShouldAddTooltipText() {
        val mapping = continuous(Aes.WIDTH)
        val targetTooltipSpec = createUnivariateFunctionBuilder()
                .variable(mapping)
                .build()

        assertText(targetTooltipSpec, mapping.longTooltipText())
    }

    @Test
    fun whenNonNumericWidthVar_ShouldAddTooltipText() {
        val mapping = discrete(Aes.WIDTH)
        val targetTooltipSpec = createUnivariateFunctionBuilder()
                .variable(mapping)
                .build()

        assertText(targetTooltipSpec, mapping.longTooltipText())
    }

    private fun createUnivariateFunctionBuilder(): TestingTooltipSpecsBuilder {
        return TestingTooltipSpecsBuilder.univariateFunctionBuilder()
    }

}
