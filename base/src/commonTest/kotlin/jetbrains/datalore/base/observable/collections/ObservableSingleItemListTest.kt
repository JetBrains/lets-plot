/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.observable.collections

import jetbrains.datalore.base.observable.collections.list.ObservableSingleItemList
import kotlin.test.*

class ObservableSingleItemListTest {
    private val list = ObservableSingleItemList<Int?>()

    @Test
    fun getItemOfEmpty() {
        assertFailsWith<DataloreIndexOutOfBoundsException> {
            assertTrue(list.isEmpty())
            list.item
        }
    }

    @Test
    fun addValue() {
        list.add(1)
        assertEquals(1, list.size)
        assertTrue(list.contains(1))
    }

    @Test
    fun setNullValue() {
        list.add(0)
        list.set(0, null)
        assertEquals(1, list.size)
        assertNull(list.item)
    }

    @Test
    fun setItemToEmptyList() {
        assertTrue(list.isEmpty())
        list.item = 0
        assertFalse(list.isEmpty())
    }

    @Test
    fun simpleSetItem() {
        list.add(0)
        list.item = 1
        assertEquals(1, list.item as Int)
    }

    @Test
    fun clearEmptyList() {
        assertTrue(list.isEmpty())
        list.clear()
        assertTrue(list.isEmpty())
    }
}