/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.observable.collections.list

import jetbrains.datalore.base.observable.collections.DataloreIndexOutOfBoundsException

class ObservableSingleItemList<ItemT> : AbstractObservableList<ItemT> {
    private var myItem: ItemT? = null
    private var myEmpty = true

    var item: ItemT
        get() = get(0)
        set(item) {
            if (myEmpty) {
                add(item)
            } else {
                set(0, item)
            }
        }

    constructor()

    constructor(item: ItemT) {
        myItem = item
        myEmpty = false
    }

    override val size: Int
        get() = if (myEmpty) 0 else 1


    override fun get(index: Int): ItemT {
        if (myEmpty || index != 0) {
            throw DataloreIndexOutOfBoundsException(index)
        }
        return myItem as ItemT
    }

    override fun checkAdd(index: Int, item: ItemT) {
        super.checkAdd(index, item)
        if (!myEmpty) {
            throw IllegalStateException("Single item list already has an item")
        }
    }

    override fun checkSet(index: Int, oldItem: ItemT, newItem: ItemT) {
        checkRemove(index, oldItem)
    }

    override fun doAdd(index: Int, item: ItemT) {
        myItem = item
        myEmpty = false
    }

    override fun doSet(index: Int, item: ItemT) {
        myItem = item
    }

    override fun doRemove(index: Int) {
        myItem = null
        myEmpty = true
    }
}