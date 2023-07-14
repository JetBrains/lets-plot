/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.tooltip

import org.jetbrains.letsPlot.core.plot.base.Aes
import jetbrains.datalore.plot.builder.tooltip.TestUtil.assertNoTooltips
import jetbrains.datalore.plot.builder.tooltip.TestUtil.continuous
import jetbrains.datalore.plot.builder.tooltip.TestUtil.discrete
import kotlin.test.Test

class GeomTargetInteractionAreaFunctionTest {

    @Test
    fun whenXIsContinuous_ShouldNotAddTooltip() {
        val targetTooltipSpec = createBuilder()
                .variable(continuous(org.jetbrains.letsPlot.core.plot.base.Aes.X))
                .build()

        assertNoTooltips(targetTooltipSpec)
    }

    @Test
    fun whenXIsNotContinuous_ShouldNotAddTooltip() {
        val targetTooltipSpec = createBuilder()
                .variable(discrete(org.jetbrains.letsPlot.core.plot.base.Aes.X))
                .build()

        assertNoTooltips(targetTooltipSpec)
    }

    private fun createBuilder(): jetbrains.datalore.plot.builder.tooltip.TestingTooltipSpecsBuilder {
        return jetbrains.datalore.plot.builder.tooltip.TestingTooltipSpecsBuilder.Companion.areaFunctionBuilder()
    }
}
