/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.commons.geometry

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.letsPlot.core.commons.geometry.TestUtil.COMPLEX_DATA
import org.jetbrains.letsPlot.core.commons.geometry.TestUtil.MEDIUM_DATA
import org.jetbrains.letsPlot.core.commons.geometry.TestUtil.SIMPLE_DATA
import kotlin.test.Test

class VisvalingamWhyattSimplificationTest {

    @Test
    fun simplificationByCountShouldNotBreakRing() {
        val indices = PolylineSimplifier.visvalingamWhyatt(SIMPLE_DATA).setCountLimit(4).indices.single()

        assertThat(indices).has(
            TestUtil.ValidRingCondition(
                SIMPLE_DATA
            )
        )
    }

    @Test
    fun simplificationByAreaShouldNotBreakRing() {
        val indices = PolylineSimplifier.visvalingamWhyatt(MEDIUM_DATA).setWeightLimit(0.001).indices.single()
        assertThat(indices).has(
            TestUtil.ValidRingCondition(
                MEDIUM_DATA
            )
        )
    }


    @Test
    fun tooManyPoints() {
        val indices = PolylineSimplifier.visvalingamWhyatt(COMPLEX_DATA).setCountLimit(13).indices.single()
        assertThat(indices)
            .has(TestUtil.ValidRingCondition(COMPLEX_DATA))
            .containsExactly(0, 17, 28, 36, 45, 53, 65, 74, 86, 93, 102, 110, 122)
    }
}