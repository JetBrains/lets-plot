/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.observable.collections

import org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent.EventType
import org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent.EventType.SET
import org.jetbrains.letsPlot.commons.intern.observable.collections.list.ObservableArrayList
import kotlin.test.*

class ObservableArrayListTest {
    private val list = ObservableArrayList<String>()
    private val events = ArrayList<CollectionItemEvent<out String>>()
    private val listener = object : CollectionAdapter<String>() {
        override fun onItemAdded(event: CollectionItemEvent<out String>) {
            events.add(event)
        }

        override fun onItemRemoved(event: CollectionItemEvent<out String>) {
            events.add(event)
        }
    }

    @BeforeTest
    fun setUp() {
        list.addListener(listener)
    }

    @Test
    fun itemAdd() {
        val item = "xyz"

        list.add(item)

        assertEvent(0, null, item, EventType.ADD)
    }

    @Test
    fun itemRemove() {
        val item = addSampleItem()

        list.remove(item)

        assertEvent(0, item, null, EventType.REMOVE)
    }

    @Test
    fun nonExistentItemRemove() {
        val item = "xyz"
        list.remove(item)

        assertEquals(0, events.size)
    }

    @Test
    fun addAtOkIndex() {
        val item = "xyz"
        list.add(0, item)
        assertEvent(0, null, item, EventType.ADD)
    }

    @Test
    fun addAtInvalidIndex() {
        assertFailsWith<IndexOutOfBoundsException> {
            try {
                list.add(1, "xyz")
            } finally {
                assertTrue(events.isEmpty())
                assertTrue(list.isEmpty())
            }
        }
    }

    @Test
    fun removeAtOkIndex() {
        val item = addSampleItem()
        val removed = list.removeAt(0)
        assertEquals(item, removed)
        assertEvent(0, item, null, EventType.REMOVE)
    }

    @Test
    fun removeAtInvalidIndex() {
        assertFailsWith<IndexOutOfBoundsException> {
            val item = addSampleItem()
            try {
                val removed = list.removeAt(1)
                assertEquals(item, removed)
            } finally {
                assertTrue(events.isEmpty())
                assertEquals(1, list.size)
            }
        }
    }

    @Test
    fun iteratorRemove() {
        addSampleItem()
        val i = list.iterator()
        i.next()
        i.remove()
        assertEvent(0, "xyz", null, EventType.REMOVE)
    }

    @Test
    fun oneHandlerEventOnSet() {
        addSampleItem()
        val counter = RecordingCollectionEventHandler<String>()
        list.addHandler(counter)
        list[0] = "abc"
        assertEquals(1, counter.counter)
        assertEquals(SET, counter.events.get(0).type)
    }

    private fun addSampleItem(): String {
        val item = "xyz"
        list.add(item)
        events.clear()
        return item
    }

    private fun assertEvent(index: Int, oldItem: String?, newItem: String?, type: EventType) {
        assertEquals(1, events.size)

        val event = events[0]
        assertEquals(
            CollectionItemEvent(
                oldItem,
                newItem,
                index,
                type
            ), event)
    }
}