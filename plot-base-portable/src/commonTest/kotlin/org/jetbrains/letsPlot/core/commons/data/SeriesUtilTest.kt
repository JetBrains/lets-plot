/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.commons.data

import jetbrains.datalore.base.assertion.assertArrayEquals
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil
import kotlin.test.Test

class SeriesUtilTest {

    @Test
    fun pickAtIndices() {
        val indices = listOf(1, 3, 77)
        val result = SeriesUtil.pickAtIndices(
            l,
            indices
        )
        assertArrayEquals(arrayOf("1", "3"), result.toTypedArray())
    }

    @Test
    fun skipAtIndices() {
        val indices = setOf(1, 3, 77)
        val result = SeriesUtil.skipAtIndices(
            l,
            indices
        )
        assertArrayEquals(arrayOf("0", "2"), result.toTypedArray())
    }

    companion object {
        private val l = listOf("0", "1", "2", "3")
    }
}