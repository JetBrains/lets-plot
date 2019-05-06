package jetbrains.datalore.visualization.plot.gog.core.scale

import org.junit.Assert.*

internal object ScaleTestUtil {
    fun assertExpandValuesPreservedInCopy(scale: Scale2<*>) {
        var scale = scale
        scale = scale.with()
                .multiplicativeExpand(0.777)
                .additiveExpand(777.0)
                .build()

        val copy = scale.with().build()
        assertEquals(scale.multiplicativeExpand, copy.multiplicativeExpand, 0.0)
        assertEquals(scale.additiveExpand, copy.additiveExpand, 0.0)
    }

    fun assertValuesInLimits(scale: Scale2<*>, vararg domainValues: Any) {
        for (v in domainValues) {
            assertTrue("Not in limits: $v", scale.isInDomainLimits(v))
        }
    }

    fun assertValuesNotInLimits(scale: Scale2<*>, vararg values: Any) {
        for (v in values) {
            assertFalse("In limits: $v", scale.isInDomainLimits(v))
        }
    }
}
