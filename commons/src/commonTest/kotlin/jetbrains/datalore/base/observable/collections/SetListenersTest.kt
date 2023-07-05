/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.observable.collections

import jetbrains.datalore.base.observable.collections.set.ObservableHashSet
import kotlin.test.assertEquals

class SetListenersTest : ListenersTestCase() {
    override fun createCollection(): MyCollection {
        return TestObservableHashSet()
    }

    override fun createThrowingOnAddCollection(): MyCollection {
        return object : TestObservableHashSet() {
            override fun doAdd(item: Int): Boolean {
                throw IllegalStateException()
            }
        }
    }

    override fun createThrowingOnRemoveCollection(): MyCollection {
        return object : TestObservableHashSet() {
            override fun doRemove(item: Int): Boolean {
                throw IllegalStateException()
            }
        }
    }

    private open class TestObservableHashSet : ObservableHashSet<Int>(), MyCollection {
        override var beforeItemAddedCallsNumber: Int = 0

        //            private set
        private var afterItemAddedCalled: Int = 0
        override var beforeItemRemovedCallsNumber: Int = 0

        //            private set
        private var afterItemRemovedCalled: Int = 0
        private var successful: Boolean = false

        override fun beforeItemAdded(item: Int?) {
            beforeItemAddedCallsNumber++
        }

        override fun afterItemAdded(item: Int?, success: Boolean) {
            afterItemAddedCalled++
            successful = success
        }

        override fun beforeItemRemoved(item: Int?) {
            beforeItemRemovedCallsNumber++
        }

        override fun afterItemRemoved(item: Int?, success: Boolean) {
            afterItemRemovedCalled++
            successful = success
        }

        override fun verifyLastSuccess(expected: Boolean) {
            assertEquals(expected, successful)
        }

        override fun verifyBeforeAfter() {
            assertEquals(afterItemAddedCalled, beforeItemAddedCallsNumber)
            assertEquals(afterItemRemovedCalled, beforeItemRemovedCallsNumber)
        }

        override fun assertContentEquals(vararg expected: Int) {
            val set = mutableSetOf<Int>()
            for (i in expected) {
                set.add(i)
            }
            assertEquals(set, this)
        }
    }
}