/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.observable.collections

import jetbrains.datalore.base.function.Value
import org.jetbrains.letsPlot.commons.intern.observable.collections.set.ObservableHashSet
import org.jetbrains.letsPlot.commons.intern.observable.collections.set.ObservableSet
import org.mockito.Mockito
import kotlin.test.*

class ObservableHashSetTest {
    private var set: ObservableSet<String?> = ObservableHashSet()

    @Suppress("UNCHECKED_CAST")
    private val listener = Mockito.mock(org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionAdapter::class.java) as CollectionListener<in String?>

    @BeforeTest
    fun init() {
        set.addListener(listener)
    }

    @Test
    fun add() {
        set.add("x")
        Mockito.verify(listener).onItemAdded(
            org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent(
                null,
                "x",
                -1,
                org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent.EventType.ADD
            )
        )
    }

    @Test
    fun remove() {
        set.add("x")
        Mockito.reset(listener)
        set.remove("x")
        Mockito.verify(listener).onItemRemoved(
            org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent(
                "x",
                null,
                -1,
                org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent.EventType.REMOVE
            )
        )
    }

    @Test
    fun clear() {
        set.add("x")
        Mockito.reset(listener)
        set.clear()
        Mockito.verify(listener).onItemRemoved(
            org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent(
                "x",
                null,
                -1,
                org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent.EventType.REMOVE
            )
        )
    }

    @Test
    fun nullValue() {
        set.add(null)
        Mockito.verify(listener).onItemAdded(
            org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent<String>(
                null,
                null,
                -1,
                org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent.EventType.ADD
            )
        )
        assertEquals(1, set.size)
        Mockito.reset(listener)
        set.remove(null)
        Mockito.verify(listener).onItemRemoved(
            org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent<String>(
                null,
                null,
                -1,
                org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent.EventType.REMOVE
            )
        )
        assertTrue(set.isEmpty())
    }

    @Test
    operator fun iterator() {
        set.add("x")
        set.add("y")
        Mockito.reset(listener)
        val i = set.iterator()
        val toRemove = i.next()
        i.remove()
        Mockito.verify(listener).onItemRemoved(
            org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent(
                toRemove,
                null,
                -1,
                org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent.EventType.REMOVE
            )
        )
        assertEquals(1, set.size)
    }

    @Test(expected = IllegalStateException::class)
    fun duplicateIteratorRemove() {
        val counter = createSetCountingBeforeRemove()
        set.add("x")
        set.add(null)
        assertEquals(2, set.size)
        val i = set.iterator()
        i.next()
        i.remove()
        counter.set(0)
        i.remove()
        try {
            i.remove()
        } finally {
            assertEquals(0, counter.get())
        }
    }

    @Test(expected = IllegalStateException::class)
    fun iteratorRemoveBeforeNext() {
        val counter = createSetCountingBeforeRemove()
        set.add("x")
        val i = set.iterator()
        try {
            i.remove()
        } finally {
            assertEquals(0, counter.get())
        }
    }

    private fun createSetCountingBeforeRemove(): Value<Int> {
        val counter = Value(0)
        set = object : ObservableHashSet<String?>() {
            override fun checkRemove(item: String?) {
                counter.set(counter.get() + 1)
            }

            override fun beforeItemRemoved(item: String?) {
                counter.set(counter.get() + 1)
            }
        }
        set.addListener(listener)
        return counter
    }

    @Test
    fun fireWhenNotAdded() {
        val afterCalled = Value(false)
        set = object : ObservableHashSet<String?>() {
            override fun doAdd(item: String?): Boolean {
                return false
            }

            override fun afterItemAdded(item: String?, success: Boolean) {
                assertEquals("x", item)
                assertFalse(success)
                afterCalled.set(true)
            }
        }
        set.addListener(listener)
        set.add("x")
        assertTrue(afterCalled.get())
        Mockito.verify(
            listener,
            Mockito.never()
        ).onItemAdded(
            org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent(
                "x",
                null,
                -1,
                org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent.EventType.REMOVE
            )
        )
    }

    @Test
    fun fireWhenNotRemoved() {
        val afterCalled = Value(false)
        set = object : ObservableHashSet<String?>() {
            override fun doRemove(item: String?): Boolean {
                return false
            }

            override fun afterItemRemoved(item: String?, success: Boolean) {
                assertEquals("x", item)
                assertFalse(success)
                afterCalled.set(true)
            }
        }
        set.add("x")
        set.addListener(listener)
        set.remove("x")
        assertTrue(afterCalled.get())
        Mockito.verify(
            listener,
            Mockito.never()
        ).onItemRemoved(
            org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent(
                "x",
                null,
                -1,
                org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent.EventType.REMOVE
            )
        )
    }
}