package jetbrains.datalore.visualization.plot.gog.core.scale

import jetbrains.datalore.visualization.plot.gog.core.render.Aes
import jetbrains.datalore.visualization.plot.gog.core.scale.ScaleTestUtil.assertValuesInLimits
import jetbrains.datalore.visualization.plot.gog.core.scale.ScaleTestUtil.assertValuesNotInLimits
import org.junit.Assert.*
import org.junit.Test

class ContinuousScaleTest {
    private fun createScale(): Scale2<*> {
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
    fun buildWithTransform() {
        val scale = createScale()

        val t = object : Transform {
            override fun apply(rawData: List<*>): List<Double?> {
                return emptyList()
            }

            override fun applyInverse(v: Double?): Double? {
                return null
            }
        }

        val scale1 = scale.with().continuousTransform(t).build()
        assertSame("Scale must be created with 'transform' object", t, scale1.transform)

        // scale
        val scale2 = scale1.with().additiveExpand(10.0).build()
        assertSame("Scale must retain its 'transform' object", t, scale2.transform)
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