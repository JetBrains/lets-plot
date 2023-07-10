/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.observable.children

import org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionAdapter
import org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent
import org.jetbrains.letsPlot.commons.intern.observable.collections.list.ObservableArrayList

open class ChildList<ParentT, ChildT : org.jetbrains.letsPlot.commons.intern.observable.children.SimpleComposite<in ParentT?, in ChildT>>(private val myParent: ParentT) :
    ObservableArrayList<ChildT>() {

    init {
        addListener(object : org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionAdapter<ChildT>() {
            override fun onItemAdded(event: org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent<out ChildT>) {
                event.newItem!!.parent().flush()
            }

            override fun onItemRemoved(event: org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent<out ChildT>) {
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
            org.jetbrains.letsPlot.commons.intern.observable.children.PositionData<ChildT> {
            override fun get(): org.jetbrains.letsPlot.commons.intern.observable.children.Position<ChildT> {
                @Suppress("NAME_SHADOWING")
                val index = indexOf(item)
                return object : org.jetbrains.letsPlot.commons.intern.observable.children.Position<ChildT> {

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