/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.scale

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.Scale
import org.jetbrains.letsPlot.core.plot.base.scale.BreaksGenerator
import org.jetbrains.letsPlot.core.plot.base.scale.ScaleBreaks
import org.jetbrains.letsPlot.core.plot.base.scale.transform.Transforms
import org.junit.Test
import kotlin.test.assertSame
import kotlin.test.assertTrue

internal class ScaleProviderBuilderTest {
    @Test
    fun withBreaksGenerator() {
        val bg = object : BreaksGenerator {
            override fun generateBreaks(domain: DoubleSpan, targetCount: Int): ScaleBreaks {
                return ScaleBreaks.EMPTY
            }

            override fun labelFormatter(domain: DoubleSpan, targetCount: Int): (Any) -> String {
                return { "hi" }
            }

            override fun defaultFormatter(domain: DoubleSpan, targetCount: Int) =
                labelFormatter(domain, targetCount)
        }

        val builder = ScaleProviderBuilder(Aes.X).breaksGenerator(bg)

        val scaleProvider = builder.build()
        // continuous scale
        val scale = scaleProvider.createScale(
            "X-scale",
            Transforms.IDENTITY,
            continuousRange = false,
            guideBreaks = null,
        )

        fun actual(scale: Scale): BreaksGenerator {
            assertTrue(
                scale.getBreaksGenerator() is Transforms.BreaksGeneratorForTransformedDomain,
                "Expected BreaksGeneratorForTransformedDomain bu was ${scale.getBreaksGenerator()::class.simpleName}"
            )
            return (scale.getBreaksGenerator() as Transforms.BreaksGeneratorForTransformedDomain).breaksGenerator
        }

        assertSame(bg, actual(scale), "Scale must be created with 'breaksGenerator' object")
    }
}