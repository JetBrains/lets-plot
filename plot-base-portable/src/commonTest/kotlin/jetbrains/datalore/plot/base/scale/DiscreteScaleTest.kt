/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.scale

import jetbrains.datalore.base.assertion.assertEquals
import jetbrains.datalore.plot.base.DiscreteTransform
import jetbrains.datalore.plot.base.scale.ScaleTestUtil.assertValuesInLimits
import jetbrains.datalore.plot.base.scale.ScaleTestUtil.assertValuesNotInLimits
import jetbrains.datalore.plot.base.scale.transform.Transforms
import kotlin.test.*

class DiscreteScaleTest {
    @Test
    fun withExpand() {
        val multiplicativeExpand = 0.777
        val additiveExpand = 777.0
        var scale = Scales.DemoAndTest.discreteDomain<Any>("Test scale", listOf("a", "b", "c"))
        scale = scale.with()
            .multiplicativeExpand(multiplicativeExpand)
            .additiveExpand(additiveExpand)
            .build()

        assertEquals(multiplicativeExpand, scale.multiplicativeExpand, 0.0)
        assertEquals(additiveExpand, scale.additiveExpand, 0.0)
    }

    @Test
    fun withExpand_SameInCopy() {
        val scale = Scales.DemoAndTest.discreteDomain<Any?>("Test scale", listOf("a", "b", "c"))
        ScaleTestUtil.assertExpandValuesPreservedInCopy(scale)
    }

    @Test
    fun withTransform() {
        val scale = Scales.DemoAndTest.discreteDomain<Any>("Test scale", listOf("a", "b", "c"))
        val t = Transforms.IDENTITY

        val scale1 = scale.with().continuousTransform(t).build()
        assertNotSame(t, scale1.transform, "'continuous transform' should be ignored")
    }

    @Test
    fun withDomainLimits() {
        var scale = Scales.DemoAndTest.discreteDomain<Any>(
            "Test scale", listOf("a", "b", "c"),
            domainLimits = listOf("b", "c", "d")
        )
        assertTrue(scale.transform.hasDomainLimits())
        assertValuesInLimits(scale, "b", "c")
//        assertValuesNotInLimits(scale, "a", "d")
        assertValuesNotInLimits(scale, "a")

        assertTrue(scale.hasBreaks())
//        assertEquals(listOf("b", "c"), scale.getScaleBreaks().domainValues)
        assertEquals(listOf("b", "c", "d"), scale.getScaleBreaks().domainValues)
    }

    @Test
    fun withEmptyDomainLimits() {
        var scale = Scales.DemoAndTest.discreteDomain<Any>(
            "Test scale", listOf("a", "b", "c"),
            domainLimits = emptyList()
        )

        assertFalse(scale.transform.hasDomainLimits())
        assertValuesInLimits(scale, "a", "b", "c")
        assertValuesNotInLimits(scale, "d")
    }

    @Test
    fun withDomainLimits_SameInCopy() {
        var scale = Scales.DemoAndTest.discreteDomain<Any>(
            "Test scale",
            listOf("a", "b", "c"),
            domainLimits = listOf("b", "c", "d")
        )

        val copy = scale.with().build()
        assertTrue(copy.transform.hasDomainLimits())
        assertValuesInLimits(scale, "b", "c")
//        assertValuesNotInLimits(scale, "a", "d")
        assertValuesNotInLimits(scale, "a")
    }

    @Test
    fun withDomainLimits_asNumbers() {
        var scale = Scales.DemoAndTest.discreteDomain<Any>(
            "Test scale", listOf("a", "b", "c"),
            domainLimits = listOf("b", "c", "d")
        )

        assertTrue(scale.hasBreaks())
        val actualBreaks = scale.getScaleBreaks().domainValues
//        assertEquals(listOf("b", "c"), actualBreaks)
        assertEquals(listOf("b", "c", "d"), actualBreaks)
//        assertEquals(listOf(0.0, 1.0), scale.transform.apply(actualBreaks))
        assertEquals(listOf(0.0, 1.0, 2.0), scale.transform.apply(actualBreaks))
    }

    @Test
    fun withDomainLimits_labels() {
        var scale = Scales.DemoAndTest.discreteDomain<Any>(
            "Test scale", listOf("a", "b", "c"),
            domainLimits = listOf("b", "c", "d")
        )
        scale = scale.with()
            .labels(listOf("a-lab", "b-lab", "c-lab"))
            .build()

        assertTrue(scale.hasBreaks())
        val scaleBreaks = scale.getScaleBreaks()
//        assertEquals(listOf("b", "c"), scaleBreaks.domainValues)
        assertEquals(listOf("b", "c", "d"), scaleBreaks.domainValues)
//        assertEquals(listOf("b-lab", "c-lab"), scaleBreaks.labels)
        assertEquals(listOf("a-lab", "b-lab", "c-lab"), scaleBreaks.labels)
    }

    @Test
    fun withDomainLimits_reversed() {
        var scale = Scales.DemoAndTest.discreteDomain<Any>(
            "Test scale", listOf("a", "b", "c"),
            domainLimits = listOf("d", "c", "b")
        )
        scale = scale.with()
            .labels(listOf("a-lab", "b-lab", "c-lab"))
            .build()

        assertTrue(scale.hasBreaks())
        val scaleBreaks = scale.getScaleBreaks()
//        assertEquals(listOf("c", "b"), scaleBreaks.domainValues)
        assertEquals(listOf("d", "c", "b"), scaleBreaks.domainValues)
//        assertEquals(listOf("c-lab", "b-lab"), scaleBreaks.labels)
        // The order is only preserved when breaks are manually specified.
        assertEquals(listOf("a-lab", "b-lab", "c-lab"), scaleBreaks.labels)
    }

    @Test
    fun withDomainLimits_inverseTransform() {
        var scale = Scales.DemoAndTest.discreteDomain<Any>(
            "Test scale", listOf("a", "b", "c"),
            domainLimits = listOf("b", "c", "d")
        )

//        assertEquals("b", scale.transform.applyInverse(-1.0))
        assertNull(scale.transform.applyInverse(-0.6))
        assertEquals("b", scale.transform.applyInverse(-0.5))
        assertEquals("b", scale.transform.applyInverse(0.0))
        assertEquals("b", scale.transform.applyInverse(0.4))

        assertEquals("c", scale.transform.applyInverse(0.5))
        assertEquals("c", scale.transform.applyInverse(1.0))
        assertEquals("c", scale.transform.applyInverse(1.4))
//        assertEquals("c", scale.transform.applyInverse(1.5))

        assertEquals("d", scale.transform.applyInverse(1.5))
        assertEquals("d", scale.transform.applyInverse(2.0))
        assertEquals("d", scale.transform.applyInverse(2.4))

        assertNull(scale.transform.applyInverse(2.5))
    }

    @Test
    fun withDuplicatesInDomain() {
        val domainValues = listOf("a", "a", "b", "c")
        val scale = Scales.DemoAndTest.discreteDomain<String>("Test scale", domainValues)

//        val outputValues = domainValues.distinct()
//        val mapper = Mappers.discrete(
//            discreteTransform = scale.transform as DiscreteTransform,
//            outputValues, "?"
//        )

        val scale2 = scale.with()
//            .mapper(mapper)
            .build()

        val transform = scale2.transform
        val transformedValues = transform.apply(domainValues)
        assertEquals(listOf(0.0, 0.0, 1.0, 2.0), transformedValues)

//        val mapper2 = scale2.mapper
//        val domainValues2 = transformedValues.map(mapper2)
//        assertEquals(domainValues, domainValues2)
    }
}
