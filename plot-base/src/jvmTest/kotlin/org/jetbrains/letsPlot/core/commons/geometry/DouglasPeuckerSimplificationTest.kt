/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.commons.geometry

import org.assertj.core.api.Assertions.assertThat
import kotlin.test.Test

class DouglasPeuckerSimplificationTest {

    @Test
    fun simplificationByCountShouldNotBreakRing() {

        val indices = PolylineSimplifier.douglasPeucker(TestUtil.SIMPLE_DATA).setCountLimit(4).indices.single()

        assertThat(indices).has(
            TestUtil.ValidRingCondition(
                TestUtil.SIMPLE_DATA
            )
        )
    }

    @Test
    fun simplificationByAreaShouldNotBreakRing() {

        val indices = PolylineSimplifier.douglasPeucker(TestUtil.MEDIUM_DATA).setWeightLimit(0.001).indices.single()

        assertThat(indices).has(
            TestUtil.ValidRingCondition(
                TestUtil.MEDIUM_DATA
            )
        )
    }

    @Test
    fun tooManyPoints() {

        val indices = PolylineSimplifier.douglasPeucker(TestUtil.COMPLEX_DATA).setCountLimit(13).indices.single()
        assertThat(indices)
                .has(TestUtil.ValidRingCondition(TestUtil.COMPLEX_DATA))
                .containsExactly(0, 10, 25, 34, 44, 50, 58, 69, 82, 95, 106, 113, 122)
    }
}