/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.mapping.framework

import kotlin.test.Test
import kotlin.test.assertEquals

class DifferenceBuilderTest {
    @Test
    fun listToEmpty() {
        assertConverges(listOf("a", "b", "c"), ArrayList())
    }


    @Test
    fun addOneItem() {
        assertConverges(listOf("a", "b", "c"), listOf("a", "c"))
    }

    @Test
    fun removeOneItem() {
        assertConverges(listOf("b", "c"), listOf("a", "b", "c"))
    }

    @Test
    fun rearrange() {
        assertConverges(listOf("a", "b", "c"), listOf("b", "c", "a"))
    }

    @Test
    fun addAndRearrange() {
        assertConverges(listOf("a", "b", "d", "e", "c"), listOf("b", "c", "a"))
    }

    private fun <ItemT> assertConverges(source: List<ItemT>, target: List<ItemT>) {
        val targetList = ArrayList(target)
        val items = DifferenceBuilder(source, targetList).build()
        for (item in items) {
            item.apply(targetList)
        }
        assertEquals(source, targetList)
    }
}
