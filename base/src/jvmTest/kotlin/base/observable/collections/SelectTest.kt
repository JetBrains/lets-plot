/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.observable.collections

import jetbrains.datalore.base.observable.collections.CollectionItemEvent.EventType.ADD
import jetbrains.datalore.base.observable.collections.CollectionItemEvent.EventType.REMOVE
import jetbrains.datalore.base.observable.collections.list.ObservableArrayList
import jetbrains.datalore.base.observable.collections.list.ObservableCollections
import jetbrains.datalore.base.observable.collections.list.ObservableList
import jetbrains.datalore.base.observable.collections.set.ObservableHashSet
import jetbrains.datalore.base.observable.event.EventHandler
import jetbrains.datalore.base.observable.property.*
import jetbrains.datalore.base.registration.Registration
import org.mockito.Mockito.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class SelectTest {

    @Test
    fun nonListenedList() {
        val src = ValueProperty(false)
        val selected = ObservableArrayList<String>()
        val res = testList(src, selected)

        testNonListened(src, selected, res)
    }

    @Test
    fun nonListenedCollection() {
        val src = ValueProperty(false)
        val selected = ObservableHashSet<String>()
        val res = testCollection(src, selected)

        testNonListened(src, selected, res)
    }

    @Test
    fun listenedList() {
        val src = ValueProperty(false)
        val selected = ObservableArrayList<String>()
        val res = testList(src, selected)

        testListened(src, selected, res)
    }

    @Test
    fun listenedCollection() {
        val src = ValueProperty(false)
        val selected = ObservableHashSet<String>()
        val res = testCollection(src, selected)

        testListened(src, selected, res)
    }

    @Test
    fun listRegistrations() {
        val pc = AtomicInteger()
        val cc = AtomicInteger()
        val src = listenersCountingProperty(pc)
        val selected = listenersCountingList(cc)

        val res = testList(src, selected)

        testRegistrations(src, res, pc, cc)
    }

    @Test
    fun collectionRegistrations() {
        val propertyListeners = AtomicInteger()
        val collectionListeners = AtomicInteger()
        val src = listenersCountingProperty(propertyListeners)
        val selected = listenersCountingList(collectionListeners)

        val res = testCollection(src, selected)

        testRegistrations(src, res, propertyListeners, collectionListeners)
    }

    @Test
    fun listInnerUnfollow() {
        val propertyListeners = AtomicInteger()
        val collectionListeners = AtomicInteger()
        val src = listenersCountingProperty(propertyListeners)
        val selected = listenersCountingList(collectionListeners)

        val res = testList(src, selected)

        val r1 = res.addListener(CollectionAdapter())
        val handlerPass = AtomicInteger()
        src.addHandler(object : EventHandler<PropertyChangeEvent<out Boolean>> {
            override fun onEvent(event: PropertyChangeEvent<out Boolean>) {
                handlerPass.incrementAndGet()
                if (handlerPass.get() == 1) {
                    r1.remove()
                }
                src.set(false)
            }
        })
        src.set(true)
    }

    @Test
    fun collectionNonEmpty() {
        val res = testCollection(Properties.TRUE, newTestList())

        res.addListener(CollectionAdapter())
        assertEquals(TEST_LIST_SIZE, res.size)
    }

    @Test
    fun listNonEmpty() {
        val res = testList(Properties.TRUE, newTestList())

        res.addListener(CollectionAdapter())
        assertEquals(TEST_LIST_SIZE, res.size)
    }

    @Test
    fun listNonEmptyIterator() {
        val res = testList(Properties.TRUE, newTestList())

        assertEquals(TEST_LIST_SIZE, res.size)
        for (s in res) {
            assertNotNull(s)
        }

        res.addListener(CollectionAdapter())
        assertEquals(TEST_LIST_SIZE, res.size)
        for (s in res) {
            assertNotNull(s)
        }
    }

    @Test
    fun collectionNonEmptyIterator() {
        val res = testCollection(Properties.TRUE, newTestList())

        assertEquals(TEST_LIST_SIZE, res.size)
        for (s in res) {
            assertNotNull(s)
        }

        res.addListener(CollectionAdapter())
        assertEquals(TEST_LIST_SIZE, res.size)
        for (s in res) {
            assertNotNull(s)
        }
    }

    private fun newTestList(): ObservableList<String> {
        val test = ObservableArrayList<String>()
        for (i in 0 until TEST_LIST_SIZE) {
            test.add(i.toString())
        }
        return test
    }

    private fun testNonListened(src: Property<Boolean>,
                                selected: ObservableCollection<String>, res: ObservableCollection<String>
    ) {
        assertEquals(0, res.size)

        selected.add("1")
        assertEquals(0, res.size)

        src.set(true)
        assertEquals(selected.size, res.size)

        selected.add("2")
        assertEquals(selected.size, res.size)

        src.set(false)
        assertEquals(0, res.size)
    }

    private fun testListened(src: Property<Boolean>,
                             selected: ObservableCollection<String>, res: ObservableCollection<String>
    ) {
        @Suppress("UNCHECKED_CAST")
        val mock = mock(CollectionListener::class.java) as CollectionListener<in String>
        res.addListener(mock)

        selected.add("1")
        verifyZeroInteractions(mock)
        assertEquals(0, res.size)

        src.set(true)
        verify<CollectionListener<in String>>(mock).onItemAdded(CollectionItemEvent(null, "1", 0, ADD))
        assertEquals(selected.size, res.size)

        selected.add("2")
        verify<CollectionListener<in String>>(mock).onItemAdded(CollectionItemEvent(null, "2", 1, ADD))
        assertEquals(selected.size, res.size)

        src.set(false)
        verify<CollectionListener<in String>>(mock).onItemRemoved(CollectionItemEvent("1", null, 0, REMOVE))
        verify<CollectionListener<in String>>(mock).onItemRemoved(CollectionItemEvent("2", null, 0, REMOVE))
        assertEquals(0, res.size)
    }

    private fun testRegistrations(src: Property<Boolean>, res: ObservableCollection<String>,
                                  propertyListeners: AtomicInteger, collectionListeners: AtomicInteger) {

        class ListenerChecker {
            fun check(prop: Int, collection: Int) {
                assertEquals(prop, propertyListeners.get())
                assertEquals(collection, collectionListeners.get())
            }
        }

        val checker = ListenerChecker()
        checker.check(0, 0)

        @Suppress("UNCHECKED_CAST")
        val mock = mock(CollectionListener::class.java) as CollectionListener<in String>
        val mockReg = res.addListener(mock)
        checker.check(1, 0)

        src.set(true)
        checker.check(1, 1)

        src.set(false)
        checker.check(1, 0)

        src.set(true)
        checker.check(1, 1)

        mockReg.remove()
        checker.check(0, 0)
    }

    private fun testList(src: ReadableProperty<out Boolean>, selected: ObservableList<String>): ObservableList<String> {

        return ObservableCollections.selectList(src) { value -> if (value) selected else ObservableCollections.emptyList() }
    }

    private fun testCollection(src: ReadableProperty<out Boolean>,
                               selected: ObservableCollection<String>
    ): ObservableCollection<String> {

        return ObservableCollections.selectCollection(src) { value -> if (value) selected else ObservableCollections.emptyList() }
    }

    private fun listenersCountingList(counter: AtomicInteger): ObservableList<String> {
        return object : ObservableArrayList<String>() {
            override fun addListener(l: CollectionListener<in String>): Registration {
                val r = super.addListener(l)
                counter.incrementAndGet()
                return object : Registration() {
                    override fun doRemove() {
                        r.remove()
                        counter.decrementAndGet()
                    }
                }
            }
        }
    }

    private fun listenersCountingProperty(counter: AtomicInteger): Property<Boolean> {
        return object : ValueProperty<Boolean>(false) {
            override fun addHandler(handler: EventHandler<PropertyChangeEvent<out Boolean>>): Registration {
                val r = super.addHandler(handler)
                counter.incrementAndGet()
                return object : Registration() {
                    override fun doRemove() {
                        r.remove()
                        counter.decrementAndGet()
                    }
                }
            }
        }
    }

    companion object {
        private const val TEST_LIST_SIZE = 3
    }
}