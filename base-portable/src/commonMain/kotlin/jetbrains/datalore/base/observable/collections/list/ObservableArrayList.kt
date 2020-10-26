/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.observable.collections.list


open class ObservableArrayList<ItemT> : AbstractObservableList<ItemT>() {
    private var myContainer: MutableList<ItemT>? = null

    override val size: Int
        get() = if (myContainer == null) 0 else myContainer!!.size

    override operator fun get(index: Int): ItemT {
        if (myContainer == null) {
            throw IndexOutOfBoundsException("$index")
        }

        return myContainer!![index]
    }

    override fun doAdd(index: Int, item: ItemT) {
        ensureContainerInitialized()
        myContainer!!.add(index, item)
    }

    override fun doSet(index: Int, item: ItemT) {
        myContainer!![index] = item
    }

    override fun doRemove(index: Int) {
        myContainer!!.removeAt(index)
        if (myContainer!!.isEmpty()) {
            myContainer = null
        }
    }

    private fun ensureContainerInitialized() {
        if (myContainer == null) {
            myContainer = ArrayList(1)
        }
    }
}