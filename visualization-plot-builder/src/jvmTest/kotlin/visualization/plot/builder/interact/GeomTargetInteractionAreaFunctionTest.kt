package jetbrains.datalore.visualization.plot.builder.interact

import jetbrains.datalore.visualization.plot.base.Aes
import jetbrains.datalore.visualization.plot.builder.interact.TestUtil.assertNoTooltips
import jetbrains.datalore.visualization.plot.builder.interact.TestUtil.continuous
import jetbrains.datalore.visualization.plot.builder.interact.TestUtil.discrete
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

    private fun createBuilder(): TestingTooltipSpecsBuilder {
        return TestingTooltipSpecsBuilder.areaFunctionBuilder()
    }
}
