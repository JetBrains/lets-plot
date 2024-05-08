/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.typedGeometry.algorithms

import org.assertj.core.api.Assertions.assertThat
import kotlin.test.Test

class GeometryTests {

    @Test
    fun `normalize empty ring`() {
        assertThat(normalizeRing(emptyList(), Int::equals))
            .isEqualTo(emptyList<Int>())
    }

    @Test
    fun `normalize ring with one element`() {
        assertThat(normalizeRing(listOf(1), Int::equals))
            .isEqualTo(listOf(1, 1))
    }

    @Test
    fun `normalize not repeating not closed ring with 2 elements`() {
        assertThat(normalizeRing(listOf(1, 2, 1), Int::equals))
            .isEqualTo(listOf(1, 2, 1))
    }

    @Test
    fun `normalize not repeating closed ring with 4 elements`() {
        assertThat(normalizeRing(listOf(1, 2, 3, 1), Int::equals))
            .isEqualTo(listOf(1, 2, 3, 1))
    }

    @Test
    fun `normalize not repeating not closed ring`() {
        assertThat(normalizeRing(listOf(1, 2, 3, 4), Int::equals))
            .isEqualTo(listOf(1, 2, 3, 4, 1))
    }

    @Test
    fun `normalize not closed simple ring`() {
        assertThat(normalizeRing(listOf(1, 1, 2, 3, 4, 4), Int::equals))
            .isEqualTo(listOf(1, 1, 1, 2, 3, 4, 4, 1))
    }

    @Test
    fun `normalize ring with 2 equal elements`() {
        assertThat(normalizeRing(listOf(1, 1), Int::equals))
            .isEqualTo(listOf(1, 1))
    }

    @Test
    fun `normalize ring with 2 different elements`() {
        assertThat(normalizeRing(listOf(1, 2), Int::equals))
            .isEqualTo(listOf(1, 2, 1))
    }

    @Test
    fun `normalize ring with 3 equal elements`() {
        assertThat(normalizeRing(listOf(1, 1, 1), Int::equals))
            .isEqualTo(listOf(1, 1, 1, 1))
    }

    @Test
    fun `normalize ring with 4 equal elements`() {
        assertThat(normalizeRing(listOf(1, 1, 1, 1), Int::equals))
            .isEqualTo(listOf(1, 1, 1, 1))
    }

    @Test
    fun `normalize ring with last different element`() {
        assertThat(normalizeRing(listOf(1, 1, 1, 1, 2), Int::equals))
            .isEqualTo(listOf(1, 1, 1, 1, 1, 2, 1))
    }


    @Test
    fun `normalize ring with last same elements`() {
        assertThat(normalizeRing(listOf(1, 2, 3, 4, 1, 1, 1), Int::equals))
            .isEqualTo(listOf(1, 2, 3, 4, 1, 1, 1))
    }

    @Test
    fun `normalize ring with two last same elements`() {
        assertThat(normalizeRing(listOf(1, 2, 3, 4, 1, 1), Int::equals))
            .isEqualTo(listOf(1, 2, 3, 4, 1, 1, 1))
    }

    @Test
    fun `normalize ring with start element in body`() {
        assertThat(normalizeRing(listOf(1, 2, 3, 1, 4, 5, 6, 1), Int::equals))
            .isEqualTo(listOf(1, 2, 3, 1, 1, 4, 5, 6, 1))
    }

    @Test
    fun `normalize ring with few start element in body`() {
        assertThat(normalizeRing(listOf(1, 2, 3, 1, 1, 4, 5, 6, 1, 1, 1, 7, 8, 1, 1, 1, 9, 10), Int::equals))
            .isEqualTo(listOf(1, 2, 3, 1, 1, 4, 5, 6, 1, 1, 1, 1, 7, 8, 1, 1, 1, 1, 9, 10, 1))
    }

}
