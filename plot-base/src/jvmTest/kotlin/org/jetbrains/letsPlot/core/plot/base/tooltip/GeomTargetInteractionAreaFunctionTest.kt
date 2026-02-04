/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.tooltip

import org.jetbrains.letsPlot.core.plot.base.Aes
import kotlin.test.Test

class GeomTargetInteractionAreaFunctionTest {

    @Test
    fun whenXIsContinuous_ShouldNotAddTooltip() {
        val targetTooltipSpec = createBuilder()
            .variable(TestUtil.continuous(Aes.X))
            .build()

        TestUtil.assertNoTooltips(targetTooltipSpec)
    }

    @Test
    fun whenXIsNotContinuous_ShouldNotAddTooltip() {
        val targetTooltipSpec = createBuilder()
            .variable(TestUtil.discrete(Aes.X))
            .build()

        TestUtil.assertNoTooltips(targetTooltipSpec)
    }

    private fun createBuilder(): TestingTooltipSpecsBuilder {
        return TestingTooltipSpecsBuilder.areaFunctionBuilder()
    }
}
