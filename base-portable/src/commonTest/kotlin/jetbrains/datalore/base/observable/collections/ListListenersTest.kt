/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.observable.collections

import jetbrains.datalore.base.observable.collections.list.ObservableArrayList
import jetbrains.datalore.base.observable.collections.list.ObservableList
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

open class ListListenersTest : ListenersTestCase() {
    override fun createCollection(): MyList {
        return TestObservableArrayList()
    }

    override fun createThrowingOnAddCollection(): MyList {
        return object : TestObservableArrayList() {
            override fun doAdd(index: Int, item: Int) {
                throw IllegalStateException()
            }
        }
    }

    protected fun createThrowingOnSetCollection(): MyList {
        return object : TestObservableArrayList() {
            override fun doSet(index: Int, item: Int) {
                throw IllegalStateException()
            }
        }
    }

    override fun createThrowingOnRemoveCollection(): MyList {
        return object : TestObservableArrayList() {
            override fun doRemove(index: Int) {
                throw IllegalStateException()
            }
        }
    }

    @Test
    fun beforeAfterAreCalledOnSet() {
        val c = createCollection()
        c.add(0)
        c[0] = 4
        c.removeAt(0)
        assertTrue(c.isEmpty())
        assertEquals(1, c.beforeItemAddedCallsNumber)
        assertEquals(1, c.beforeItemSetCallsNumber)
        assertEquals(1, c.beforeItemRemovedCallsNumber)
        c.verifyBeforeAfter()
    }

    @Test
    fun beforeAfterOnSetArentAffectedByListenerExceptions() {
        assertFailsWith<UnsupportedOperationException> {
            val c = createCollection()
            c.add(0)
            c.addListener(createThrowingListener())
            try {
                c[0] = 5
            } finally {
                c.verifyBeforeAfter()
                c.verifyLastSuccess(true)
                c.assertContentEquals(5)
            }
        }
    }

    @Test
    fun setFailureDoesntAffectBeforeAfter() {
        assertFailsWith<IllegalStateException> {
            val c = createThrowingOnSetCollection()
            c.add(0)
            c.addListener(createThrowingListener())
            try {
                c[0] = 5
            } finally {
                c.verifyBeforeAfter()
                c.verifyLastSuccess(false)
                c.assertContentEquals(0)
            }
        }
    }

    protected interface MyList : MyCollection, ObservableList<Int> {
        val beforeItemSetCallsNumber: Int
    }

    private open class TestObservableArrayList : ObservableArrayList<Int>(), MyList {
        override var beforeItemAddedCallsNumber: Int = 0
        //            private set
        private var afterItemAddedCalled: Int = 0
        override var beforeItemSetCallsNumber: Int = 0
        //            private set
        private var afterItemSetCalled: Int = 0
        override var beforeItemRemovedCallsNumber: Int = 0
        //            private set
        private var afterItemRemovedCalled: Int = 0
        private var successful: Boolean = false

        override fun beforeItemAdded(index: Int, item: Int) {
            beforeItemAddedCallsNumber++
        }

        override fun afterItemAdded(index: Int, item: Int, success: Boolean) {
            afterItemAddedCalled++
            successful = success
        }

        override fun beforeItemSet(index: Int, oldItem: Int, newItem: Int) {
            beforeItemSetCallsNumber++
        }

        override fun afterItemSet(index: Int, oldItem: Int, newItem: Int, success: Boolean) {
            afterItemSetCalled++
            successful = success
        }

        override fun beforeItemRemoved(index: Int, item: Int) {
            beforeItemRemovedCallsNumber++
        }

        override fun afterItemRemoved(index: Int, item: Int, success: Boolean) {
            afterItemRemovedCalled++
            successful = success
        }

        override fun verifyLastSuccess(expected: Boolean) {
            assertEquals(expected, successful)
        }

        override fun verifyBeforeAfter() {
            assertEquals(afterItemAddedCalled, beforeItemAddedCallsNumber)
            assertEquals(afterItemSetCalled, beforeItemSetCallsNumber)
            assertEquals(afterItemRemovedCalled, beforeItemRemovedCallsNumber)
        }

        override fun assertContentEquals(vararg expected: Int) {
            val list = mutableListOf<Int>()
            for (i in expected) {
                list.add(i)
            }
            assertEquals(list, this)
        }
    }
}