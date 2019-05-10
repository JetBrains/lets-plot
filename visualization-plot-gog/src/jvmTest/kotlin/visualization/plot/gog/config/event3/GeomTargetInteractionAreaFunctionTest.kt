package jetbrains.datalore.visualization.plot.gog.config.event3

import jetbrains.datalore.visualization.plot.gog.config.event3.TestUtil.assertNoTooltips
import jetbrains.datalore.visualization.plot.gog.config.event3.TestUtil.continuous
import jetbrains.datalore.visualization.plot.gog.config.event3.TestUtil.discrete
import jetbrains.datalore.visualization.plot.gog.core.render.Aes
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

    private fun createBuilder(): TargetTooltipSpecBuilder {
        return TargetTooltipSpecBuilder.areaFunctionBuilder()
    }
}
