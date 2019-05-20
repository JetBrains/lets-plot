package jetbrains.datalore.visualization.plot.gog.config.event3

import jetbrains.datalore.visualization.plot.base.render.Aes
import jetbrains.datalore.visualization.plot.gog.config.event3.TestUtil.assertNoTooltips
import jetbrains.datalore.visualization.plot.gog.config.event3.TestUtil.assertText
import jetbrains.datalore.visualization.plot.gog.config.event3.TestUtil.continuous
import jetbrains.datalore.visualization.plot.gog.config.event3.TestUtil.discrete
import kotlin.test.Test

class GeomTargetInteractionBivariateFunctionTest {

    @Test
    fun withNoMappings_ShouldNotAddTooltipText() {
        val targetTooltipSpec = createBuilder()
                .build()

        assertNoTooltips(targetTooltipSpec)
    }

    @Test
    fun whenXIsContinuous_ShouldAddTooltipText() {
        val mapping = continuous(Aes.X)
        val targetTooltipSpec = createBuilder()
                .variable<Double>(mapping)
                .build()

        assertText(targetTooltipSpec, mapping.shortTooltipText())
    }

    @Test
    fun whenXIsNotContinuous_ShouldNotAddTooltipText() {
        val targetTooltipSpec = createBuilder()
                .variable(discrete(Aes.X))
                .build()

        assertNoTooltips(targetTooltipSpec)
    }

    @Test
    fun whenYIsContinuous_ShouldAddTooltipText() {
        val mapping = continuous(Aes.Y)
        val targetTooltipSpec = createBuilder()
                .variable<Double>(mapping)
                .build()

        assertText(targetTooltipSpec, mapping.shortTooltipText())
    }

    @Test
    fun whenYIsNotContinuous_ShouldNotAddTooltipText() {
        val targetTooltipSpec = createBuilder()
                .variable(discrete(Aes.Y))
                .build()

        assertNoTooltips(targetTooltipSpec)
    }

    @Test
    fun whenWidthIsContinuous_ShouldAddTooltipText() {
        val mapping = continuous(Aes.WIDTH)
        val targetTooltipSpec = createBuilder()
                .variable<Double>(mapping)
                .build()

        assertText(targetTooltipSpec, mapping.longTooltipText())
    }

    @Test
    fun whenWidthIsNotContinuous_ShouldAddTooltipText() {
        val mapping = discrete(Aes.WIDTH)
        val targetTooltipSpec = createBuilder()
                .variable<Double>(mapping)
                .build()

        assertText(targetTooltipSpec, mapping.longTooltipText())
    }

    private fun createBuilder(): TargetTooltipSpecBuilder {
        return TargetTooltipSpecBuilder.bivariateFunctionBuilder()
    }
}
