package jetbrains.datalore.visualization.plot.base.scale

import jetbrains.datalore.base.assertion.assertEquals
import jetbrains.datalore.visualization.plot.base.scale.ScaleTestUtil.assertValuesInLimits
import jetbrains.datalore.visualization.plot.base.scale.ScaleTestUtil.assertValuesNotInLimits
import jetbrains.datalore.visualization.plot.base.scale.transform.Transforms
import kotlin.test.*

class DiscreteScaleTest {
    @Test
    fun withExpand() {
        val multiplicativeExpand = 0.777
        val additiveExpand = 777.0
        var scale = Scales.discreteDomain<Any>("Test scale", listOf("a", "b", "c"))
        scale = scale.with()
                .multiplicativeExpand(multiplicativeExpand)
                .additiveExpand(additiveExpand)
                .build()

        assertEquals(multiplicativeExpand, scale.multiplicativeExpand, 0.0)
        assertEquals(additiveExpand, scale.additiveExpand, 0.0)
    }

    @Test
    fun withExpand_SameInCopy() {
        val scale = Scales.discreteDomain<Any?>("Test scale", listOf("a", "b", "c"))
        ScaleTestUtil.assertExpandValuesPreservedInCopy(scale)
    }

    @Test
    fun buildWithTransform() {
        val scale = Scales.discreteDomain<Any>("Test scale", listOf("a", "b", "c"))
        val t = Transforms.IDENTITY

        val scale1 = scale.with().continuousTransform(t).build()
        assertNotSame(scale1.transform, t, "'continuous transform' should be ignored")
    }

    @Test
    fun withDomainLimits() {
        var scale = Scales.discreteDomain<Any>("Test scale", listOf("a", "b", "c"))
        scale = scale.with()
                .limits(setOf("b", "c", "d"))
                .build()

        assertTrue(scale.hasDomainLimits())
        assertValuesInLimits(scale, "b", "c")
        assertValuesNotInLimits(scale, "a", "d")

        assertTrue(scale.hasBreaks())
        assertEquals(listOf("b", "c"), scale.breaks)
    }

    @Test
    fun withEmptyDomainLimits() {
        var scale = Scales.discreteDomain<Any>("Test scale", listOf("a", "b", "c"))
        scale = scale.with()
                .limits(emptySet<Any>())
                .build()

        assertFalse(scale.hasDomainLimits())
        assertValuesInLimits(scale, "a", "b", "c")
        assertValuesNotInLimits(scale, "d")
    }

    @Test
    fun withDomainLimits_SameInCopy() {
        var scale = Scales.discreteDomain<Any>("Test scale", listOf("a", "b", "c"))
        scale = scale.with()
                .limits(setOf("b", "c", "d"))
                .build()

        val copy = scale.with().build()
        assertTrue(copy.hasDomainLimits())
        assertValuesInLimits(scale, "b", "c")
        assertValuesNotInLimits(scale, "a", "d")
    }

    @Test
    fun withDomainLimits_asNumbers() {
        var scale = Scales.discreteDomain<Any>("Test scale", listOf("a", "b", "c"))
        scale = scale.with()
                .limits(setOf("b", "c", "d"))
                .build()

        assertTrue(scale.hasBreaks())
        assertEquals(listOf("b", "c"), scale.breaks)
        assertEquals(listOf(0.0, 1.0), ScaleUtil.breaksAsNumbers(scale))
    }

    @Test
    fun withDomainLimits_inverseTransform() {
        var scale = Scales.discreteDomain<Any>("Test scale", listOf("a", "b", "c"))
        scale = scale.with()
                .limits(setOf("b", "c", "d"))
                .build()

        assertEquals("b", scale.transform.applyInverse(-1.0))
        assertEquals("b", scale.transform.applyInverse(0.0))
        assertEquals("b", scale.transform.applyInverse(0.4))

        assertEquals("c", scale.transform.applyInverse(0.6))
        assertEquals("c", scale.transform.applyInverse(1.0))
        assertEquals("c", scale.transform.applyInverse(1.5))
    }

    @Test
    fun withDuplicatesInDomain() {
        val domainValues = listOf("a", "a", "b", "c")
        val distinctDomainValues = ArrayList(LinkedHashSet(domainValues))

        var scale = Scales.discreteDomain<String>("Test scale", domainValues)
        scale = scale.with()
                .mapper(Mappers.discrete(distinctDomainValues, "?"))
                .build()

        val transform = scale.transform
        val transformedValues = transform.apply(domainValues)
        assertEquals(listOf(0.0, 0.0, 1.0, 2.0), transformedValues)

        val mapper = scale.mapper
        val domainValues2 = transformedValues.map(mapper)
        assertEquals(domainValues, domainValues2)
    }
}
