package jetbrains.datalore.plot.builder.interact

import jetbrains.datalore.plot.builder.interact.TestUtil.assertNoTooltips
import jetbrains.datalore.plot.builder.interact.TestUtil.continuous
import jetbrains.datalore.plot.builder.interact.TestUtil.discrete
import jetbrains.datalore.visualization.plot.base.Aes
import kotlin.test.Test

class GeomTargetInteractionAreaFunctionTest {

    @Test
    fun whenXIsContinuous_ShouldNotAddTooltip() {
        val targetTooltipSpec = createBuilder()
                .variable(continuous(Aes.X))
                .build()

        assertNoTooltips(targetTooltipSpec)
    }

    @Test
    fun whenXIsNotContinuous_ShouldNotAddTooltip() {
        val targetTooltipSpec = createBuilder()
                .variable(discrete(Aes.X))
                .build()

        assertNoTooltips(targetTooltipSpec)
    }

    private fun createBuilder(): jetbrains.datalore.plot.builder.interact.TestingTooltipSpecsBuilder {
        return jetbrains.datalore.plot.builder.interact.TestingTooltipSpecsBuilder.Companion.areaFunctionBuilder()
    }
}
