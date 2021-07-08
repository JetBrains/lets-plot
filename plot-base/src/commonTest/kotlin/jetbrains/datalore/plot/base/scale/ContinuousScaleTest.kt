/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.scale

import jetbrains.datalore.base.assertion.assertEquals
import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.base.Transform
import jetbrains.datalore.plot.base.scale.ScaleTestUtil.assertValuesInLimits
import jetbrains.datalore.plot.base.scale.ScaleTestUtil.assertValuesNotInLimits
import jetbrains.datalore.plot.base.scale.transform.Transforms
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

class ContinuousScaleTest {
    private fun createScale(): Scale<*> {
        return Scales.continuousDomain("Test scale", Aes.X)
    }

    @Test
    fun withExpand() {
        val multiplicativeExpand = 0.777
        val additiveExpand = 777.0
        var scale = createScale()
        scale = scale.with()
            .multiplicativeExpand(multiplicativeExpand)
            .additiveExpand(additiveExpand)
            .upperLimit(10.0)
            .build()

        assertEquals(multiplicativeExpand, scale.multiplicativeExpand, 0.0)
        assertEquals(additiveExpand, scale.additiveExpand, 0.0)
    }

    @Test
    fun withExpand_SameInCopy() {
        val scale = createScale()
        ScaleTestUtil.assertExpandValuesPreservedInCopy(scale)
    }

    @Test
    fun withTransform() {
        val scale = createScale()

        val t = object : Transform {
            override fun apply(l: List<*>): List<Double?> {
                return emptyList()
            }

            override fun applyInverse(v: Double?): Double? {
                return null
            }
        }

        val scale1 = scale.with().continuousTransform(t).build()
        assertSame(t, scale1.transform, "Scale must be created with 'transform' object")

        // change something else...
        val scale2 = scale1.with().additiveExpand(10.0).build()
        assertSame(t, scale2.transform, "Scale must retain its 'transform' object")
    }

    @Test
    fun withBreaksGenerator() {
        val scale = createScale()

        val bg = object : BreaksGenerator {
            override fun generateBreaks(domain: ClosedRange<Double>, targetCount: Int): ScaleBreaks {
                return ScaleBreaks(emptyList(), emptyList(), emptyList())
            }

            override fun labelFormatter(domain: ClosedRange<Double>, targetCount: Int): (Any) -> String {
                return { "hi" }
            }
        }

        fun actual(scale: Scale<*>): BreaksGenerator {
            assertTrue(
                scale.breaksGenerator is Transforms.BreaksGeneratorForTransformedDomain,
                "Expected BreaksGeneratorForTransformedDomain bu was ${scale.breaksGenerator::class.simpleName}"
            )
            return (scale.breaksGenerator as Transforms.BreaksGeneratorForTransformedDomain).breaksGenerator
        }

        val scale1 = scale.with().breaksGenerator(bg).build()
        assertSame(bg, actual(scale1), "Scale must be created with 'breaksGenerator' object")

        // change something else...
        val scale2 = scale1.with().additiveExpand(10.0).build()
        assertSame(bg, actual(scale2), "Scale must retain its 'breaksGenerator' object")
    }

    @Test
    fun withDomainLimits() {
        var scale = createScale()
        scale = scale.with()
            .lowerLimit(-10.0)
            .upperLimit(10.0)
            .build()

        assertTrue(scale.hasDomainLimits())
        assertValuesInLimits(scale, -10, 0.0, 10.0)
        assertValuesNotInLimits(scale, -11, 11.0)
    }

    @Test
    fun withDomainLimits_Lower() {
        var scale = createScale()
        scale = scale.with()
            .lowerLimit(-10.0)
            .build()

        assertTrue(scale.hasDomainLimits())
        assertValuesInLimits(scale, -10, 0.0, 10.0, 11)
        assertValuesNotInLimits(scale, -11)
    }

    @Test
    fun withDomainLimits_Upper() {
        var scale = createScale()
        scale = scale.with()
            .upperLimit(10.0)
            .build()

        assertTrue(scale.hasDomainLimits())
        assertValuesInLimits(scale, -11, -10, 0.0, 10.0)
        assertValuesNotInLimits(scale, 11)
    }

    @Test
    fun withDomainLimits_SameInCopy() {
        var scale = createScale()
        scale = scale.with()
            .lowerLimit(-10.0)
            .upperLimit(10.0)
            .build()

        val domainLimits = scale.domainLimits

        val copy = scale.with().build()
        assertTrue(copy.hasDomainLimits())
        assertEquals(domainLimits, copy.domainLimits)
    }
}