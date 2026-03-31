package org.jetbrains.letsPlot.commons.intern

import org.assertj.core.api.Assertions.assertThat
import kotlin.test.Test

class CollectionExTest {

    @Test
    fun `splitBy empty list`() {
        assertThat(emptyList<Int>().splitBy( compareBy { it == 0 }))
            .isEqualTo(emptyList<List<Int>>())
    }

    @Test
    fun `splitBy 0`() {
        assertThat(listOf(0, 1, 2, 0, 0, 1, 2, 0, 0 ,0, 1, 2, 0, 0, 0).splitBy( compareBy { it == 0 }))
            .isEqualTo(listOf(listOf(0), listOf(1, 2), listOf(0, 0), listOf(1, 2), listOf(0, 0, 0), listOf(1, 2), listOf(0, 0, 0)))
    }

    @Test
    fun `splitByNull empty list`() {
        assertThat(emptyList<Int?>().splitByNull())
            .isEqualTo(emptyList<List<Int>>())
    }

    @Test
    fun `splitByNull list of null`() {
        assertThat(listOf<Int?>(null,null,null,null,null,null).splitByNull())
            .isEqualTo(emptyList<List<Int>>())
    }

    @Test
    fun `splitByNull list with one NotNull `() {
        assertThat(listOf(null,null,1,null,null,null).splitByNull())
            .isEqualTo(listOf(listOf(1)))
    }

    @Test
    fun `splitByNull list with null`() {
        assertThat(listOf(null, 1, 2, null, null, 1, 2, null, null ,null, 1, 2, null, null, null).splitByNull())
            .isEqualTo(listOf(listOf(1, 2), listOf(1, 2), listOf(1, 2)))
    }
}