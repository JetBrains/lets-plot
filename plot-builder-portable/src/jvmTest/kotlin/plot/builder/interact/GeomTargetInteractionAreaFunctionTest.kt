/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.builder.interact.TestUtil.assertNoTooltips
import jetbrains.datalore.plot.builder.interact.TestUtil.continuous
import jetbrains.datalore.plot.builder.interact.TestUtil.discrete
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
