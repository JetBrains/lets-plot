/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.observable.collections

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

abstract class ListenersTestCase {

    protected abstract fun createCollection(): MyCollection
    protected abstract fun createThrowingOnAddCollection(): MyCollection
    protected abstract fun createThrowingOnRemoveCollection(): MyCollection

    @Test
    fun beforeAfterAreCalled() {
        val c = createCollection()
        c.add(0)
        c.remove(0)
        assertTrue(c.isEmpty())
        assertEquals(1, c.beforeItemAddedCallsNumber)
        assertEquals(1, c.beforeItemRemovedCallsNumber)
        c.verifyBeforeAfter()
    }

    @Test
    fun beforeAfterOnAddArentAffectedByListenerExceptions() {
        assertFailsWith<UnsupportedOperationException> {
            val c = createCollection()
            c.addListener(createThrowingListener())
            try {
                c.add(0)
            } finally {
                c.verifyBeforeAfter()
                c.verifyLastSuccess(true)
                c.assertContentEquals(0)
            }
        }
    }

    @Test
    fun beforeAfterOnRemoveArentAffectedByListenerExceptions() {
        assertFailsWith<UnsupportedOperationException> {
            val c = createCollection()
            c.add(0)
            c.addListener(createThrowingListener())
            try {
                c.remove(0)
            } finally {
                c.verifyBeforeAfter()
                c.verifyLastSuccess(true)
                assertTrue(c.isEmpty())
            }
        }
    }

    @Test
    fun addFailureDoesntAffectBeforeAfter() {
        assertFailsWith<IllegalStateException> {
            val c = createThrowingOnAddCollection()
            c.addListener(createThrowingListener())
            try {
                c.add(0)
            } finally {
                c.verifyBeforeAfter()
                c.verifyLastSuccess(false)
                assertTrue(c.isEmpty())
            }
        }
    }

    @Test
    fun removeFailureDoesntAffectBeforeAfter() {
        assertFailsWith<IllegalStateException> {
            val c = createThrowingOnRemoveCollection()
            c.add(0)
            c.addListener(createThrowingListener())
            try {
                c.remove(0)
            } finally {
                c.verifyBeforeAfter()
                c.verifyLastSuccess(false)
                c.assertContentEquals(0)
            }
        }
    }

    protected fun createThrowingListener(): CollectionListener<Int> {
        return object : CollectionListener<Int> {
            override fun onItemAdded(event: CollectionItemEvent<out Int>) {
                throw UnsupportedOperationException()
            }

            override fun onItemSet(event: CollectionItemEvent<out Int>) {
                throw UnsupportedOperationException()
            }

            override fun onItemRemoved(event: CollectionItemEvent<out Int>) {
                throw UnsupportedOperationException()
            }
        }
    }

    protected interface MyCollection :
        ObservableCollection<Int> {
        val beforeItemAddedCallsNumber: Int
        val beforeItemRemovedCallsNumber: Int
        fun verifyLastSuccess(expected: Boolean)
        fun verifyBeforeAfter()
        fun assertContentEquals(vararg expected: Int)
    }
}