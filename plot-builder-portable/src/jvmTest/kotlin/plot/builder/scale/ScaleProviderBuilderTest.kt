/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.scale

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.base.scale.BreaksGenerator
import jetbrains.datalore.plot.base.scale.ScaleBreaks
import jetbrains.datalore.plot.base.scale.transform.Transforms
import org.junit.Test
import kotlin.test.assertSame
import kotlin.test.assertTrue

internal class ScaleProviderBuilderTest {
    @Test
    fun withBreaksGenerator() {
        val bg = object : BreaksGenerator {
            override fun generateBreaks(domain: ClosedRange<Double>, targetCount: Int): ScaleBreaks {
                return ScaleBreaks.EMPTY
            }

            override fun labelFormatter(domain: ClosedRange<Double>, targetCount: Int): (Any) -> String {
                return { "hi" }
            }

            override fun defaultFormatter(domain: ClosedRange<Double>, targetCount: Int) = labelFormatter(domain, targetCount)
        }

        val builder = ScaleProviderBuilder(Aes.X).breaksGenerator(bg)

        val scaleProvider = builder.build()
        // continuous scale
        val scale = scaleProvider.createScale("X-scale", ClosedRange.singleton(0.0))


        fun actual(scale: Scale<*>): BreaksGenerator {
            assertTrue(
                scale.getBreaksGenerator() is Transforms.BreaksGeneratorForTransformedDomain,
                "Expected BreaksGeneratorForTransformedDomain bu was ${scale.getBreaksGenerator()::class.simpleName}"
            )
            return (scale.getBreaksGenerator() as Transforms.BreaksGeneratorForTransformedDomain).breaksGenerator
        }

        assertSame(bg, actual(scale), "Scale must be created with 'breaksGenerator' object")
    }
}