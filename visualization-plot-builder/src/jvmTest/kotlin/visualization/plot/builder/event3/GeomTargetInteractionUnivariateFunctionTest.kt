package jetbrains.datalore.visualization.plot.builder.event3

import jetbrains.datalore.visualization.plot.base.render.Aes
import jetbrains.datalore.visualization.plot.builder.event3.TestUtil.assertNoTooltips
import jetbrains.datalore.visualization.plot.builder.event3.TestUtil.assertText
import jetbrains.datalore.visualization.plot.builder.event3.TestUtil.continuous
import jetbrains.datalore.visualization.plot.builder.event3.TestUtil.discrete
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

    private fun createUnivariateFunctionBuilder(): TargetTooltipSpecBuilder {
        return TargetTooltipSpecBuilder.univariateFunctionBuilder()
    }

}
