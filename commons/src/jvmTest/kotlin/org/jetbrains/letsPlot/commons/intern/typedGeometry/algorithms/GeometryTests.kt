/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.typedGeometry.algorithms

import org.assertj.core.api.Assertions.assertThat
import kotlin.test.Test

class GeometryTests {

    @Test
    fun `trim not repeating not closed ring with 2 elements`() {
        assertThat(trimRing(listOf(1, 2), Int::equals))
            .isEqualTo(listOf(1, 2))
    }

    @Test
    fun `trim not repeating closed ring with 4 elements`() {
        assertThat(trimRing(listOf(1, 2, 3, 1), Int::equals))
            .isEqualTo(listOf(1, 2, 3, 1))
    }

    @Test
    fun `trim not repeating not closed ring`() {
        assertThat(trimRing(listOf(1, 2, 3, 4), Int::equals))
            .isEqualTo(listOf(1, 2, 3, 4))
    }

    @Test
    fun `trim not closed simple ring`() {
        assertThat(trimRing(listOf(1, 1, 2, 3, 4, 4), Int::equals))
            .isEqualTo(listOf(1, 2, 3, 4))
    }

    @Test
    fun `trim ring with 2 equal elements`() {
        assertThat(trimRing(listOf(1, 1), Int::equals))
            .isEqualTo(listOf(1, 1))
    }

    @Test
    fun `trim ring with 3 equal elements`() {
        assertThat(trimRing(listOf(1, 1, 1), Int::equals))
            .isEqualTo(listOf(1, 1))
    }

    @Test
    fun `trim ring with 4 equal elements`() {
        assertThat(trimRing(listOf(1, 1, 1, 1), Int::equals))
            .isEqualTo(listOf(1, 1))
    }

    @Test
    fun `trim ring with last different element`() {
        assertThat(trimRing(listOf(1, 1, 1, 1, 2), Int::equals))
            .isEqualTo(listOf(1, 2))
    }

    @Test
    fun `trim empty ring`() {
        assertThat(trimRing(emptyList(), Int::equals))
            .isEqualTo(emptyList<Int>())
    }

}
