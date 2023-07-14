/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.scale

import jetbrains.datalore.base.assertion.assertEquals
import org.jetbrains.letsPlot.core.plot.base.Scale
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal object ScaleTestUtil {
    fun assertExpandValuesPreservedInCopy(scale: Scale) {
        @Suppress("NAME_SHADOWING")
        var scale = scale
        scale = scale.with()
            .multiplicativeExpand(0.777)
            .additiveExpand(777.0)
            .build()

        val copy = scale.with().build()
        assertEquals(scale.multiplicativeExpand, copy.multiplicativeExpand, 0.0)
        assertEquals(scale.additiveExpand, copy.additiveExpand, 0.0)
    }

    fun assertValuesInLimits(scale: Scale, vararg domainValues: Any) {
        for (v in domainValues) {
//            assertTrue(scale.isInDomainLimits(v), "Not in limits: $v")
            assertTrue(scale.transform.isInDomain(v), "Not in limits: $v")
        }
    }

    fun assertValuesNotInLimits(scale: Scale, vararg values: Any) {
        for (v in values) {
//            assertFalse(scale.isInDomainLimits(v), "In limits: $v")
            assertFalse(scale.transform.isInDomain(v), "In limits: $v")
        }
    }
}
