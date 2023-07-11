/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.observable.collections.set


open class ObservableHashSet<ItemT> : AbstractObservableSet<ItemT>() {
    override val size: Int
        get() = mySet?.size ?: 0

    private var mySet: MutableSet<ItemT>? = null

    override val actualIterator: MutableIterator<ItemT>
        get() = mySet!!.iterator()

    override operator fun contains(element: ItemT): Boolean {
        return mySet?.contains(element) ?: false
    }

    override fun doAdd(item: ItemT): Boolean {
        ensureSetInitialized()
        return mySet!!.add(item)
    }

    override fun doRemove(item: ItemT): Boolean {
        return mySet!!.remove(item)
    }

    private fun ensureSetInitialized() {
        if (mySet == null) {
            mySet = HashSet(1)
        }
    }
}