/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.observable.children

import jetbrains.datalore.base.observable.collections.CollectionAdapter
import jetbrains.datalore.base.observable.collections.CollectionItemEvent
import jetbrains.datalore.base.observable.collections.list.ObservableArrayList

open class ChildList<ParentT, ChildT : SimpleComposite<in ParentT?, in ChildT>>(private val myParent: ParentT) :
    ObservableArrayList<ChildT>() {

    init {
        addListener(object : CollectionAdapter<ChildT>() {
            override fun onItemAdded(event: CollectionItemEvent<out ChildT>) {
                event.newItem!!.parent().flush()
            }

            override fun onItemRemoved(event: CollectionItemEvent<out ChildT>) {
                val item = event.oldItem
                item!!.parent().set(null)
                item.setPositionData(null)
                item.parent().flush()
            }
        })
    }

    override fun checkAdd(index: Int, item: ChildT) {
        super.checkAdd(index, item)
        if (item.parent().get() != null) {
            throw IllegalArgumentException()
        }
    }

    override fun beforeItemAdded(index: Int, item: ChildT) {
        item.parent().set(myParent)
        item.setPositionData(object :
            PositionData<ChildT> {
            override fun get(): Position<ChildT> {
                @Suppress("NAME_SHADOWING")
                val index = indexOf(item)
                return object : Position<ChildT> {

                    override val role: Any
                        get() = this@ChildList

                    override fun get(): ChildT? {
                        return if (size <= index) null else this@ChildList.get(index)
                    }
                }
            }

            override fun remove() {
                this@ChildList.remove(item)
            }
        })
    }

    override fun checkSet(index: Int, oldItem: ChildT, newItem: ChildT) {
        super.checkSet(index, oldItem, newItem)
        checkRemove(index, oldItem)
        checkAdd(index, newItem)
    }

    override fun beforeItemSet(index: Int, oldItem: ChildT, newItem: ChildT) {
        beforeItemAdded(index, newItem)
    }

    override fun checkRemove(index: Int, item: ChildT) {
        super.checkRemove(index, item)
        if (item.parent().get() !== myParent) {
            throw IllegalArgumentException()
        }
    }
}