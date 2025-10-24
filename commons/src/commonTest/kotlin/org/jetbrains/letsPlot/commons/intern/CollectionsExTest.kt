/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern

import kotlin.math.abs
import kotlin.test.*

class CollectionsExTest {
    @Test
    fun testBracketingIndicesBasic() {
        val bracketingIndices = listOf(1.0, 3.0, 5.0).bracketingIndicesOrNull(2.0)
        assertNotNull(bracketingIndices)
        val (i, j) = bracketingIndices
        assertEquals(0, i)
        assertEquals(1, j)
    }

    @Test
    fun testBracketingIndicesTwoElementsInTheList() {
        val bracketingIndices = listOf(1.0, 3.0).bracketingIndicesOrNull(2.0)
        assertNotNull(bracketingIndices)
        val (i, j) = bracketingIndices
        assertEquals(0, i)
        assertEquals(1, j)
    }

    @Test
    fun testBracketingIndicesValueInTheList() {
        val bracketingIndices = listOf(1.0, 3.0, 5.0).bracketingIndicesOrNull(3.0)
        assertNotNull(bracketingIndices)
        val (i, j) = bracketingIndices
        assertEquals(0, i)
        assertEquals(1, j)
    }

    @Test
    fun testBracketingIndicesValueEqualToFirstElement() {
        val bracketingIndices = listOf(1.0, 3.0, 5.0).bracketingIndicesOrNull(1.0)
        assertNotNull(bracketingIndices)
        val (i, j) = bracketingIndices
        assertEquals(0, i)
        assertEquals(1, j)
    }

    @Test
    fun testBracketingIndicesValueEqualToLastElement() {
        val bracketingIndices = listOf(1.0, 3.0, 5.0).bracketingIndicesOrNull(5.0)
        assertNotNull(bracketingIndices)
        val (i, j) = bracketingIndices
        assertEquals(1, i)
        assertEquals(2, j)
    }

    @Test
    fun testBracketingIndicesTooSmallList() {
        val bracketingIndices = listOf(1.0).bracketingIndicesOrNull(5.0)
        assertNull(bracketingIndices)
    }

    @Test
    fun testBracketingIndicesValueSmallerThanFirstElement() {
        val bracketingIndices = listOf(1.0, 3.0, 5.0).bracketingIndicesOrNull(0.0)
        assertNull(bracketingIndices)
    }

    @Test
    fun testBracketingIndicesValueBiggerThanLastElement() {
        val bracketingIndices = listOf(1.0, 3.0, 5.0).bracketingIndicesOrNull(6.0)
        assertNull(bracketingIndices)
    }

    @Test
    fun testBracketingIndicesWithCustomComparator() {
        val bracketingIndices = listOf(1.0, 3.0, 5.0).bracketingIndicesOrNull(
            element = 0.999,
            comparator = Comparator { a, b ->
                when {
                    abs(a - b) < 0.01 -> 0
                    a < b -> -1
                    else -> 1
                }
            }
        )
        assertNotNull(bracketingIndices)
        val (i, j) = bracketingIndices
        assertEquals(0, i)
        assertEquals(1, j)
    }
}