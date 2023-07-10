/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.mapping.framework

import org.jetbrains.letsPlot.commons.intern.observable.collections.list.ObservableArrayList
import org.jetbrains.letsPlot.commons.intern.observable.collections.list.ObservableList
import org.jetbrains.letsPlot.commons.intern.observable.property.Property
import org.jetbrains.letsPlot.commons.intern.observable.property.ValueProperty

internal class Item {

    val observableChildren: ObservableList<Item> = ObservableArrayList()
    val children: MutableList<Item> = ArrayList()
    val transformedChildren: ObservableList<Item> = ObservableArrayList()
    val singleChild: Property<Item?> = ValueProperty(null)
    val name: Property<String?> = ValueProperty(null)

    private fun contentsEqual(item1: Item?, item2: Item?): Boolean {
        return item1 === item2 || item1 != null && item1.contentEquals(item2!!)
    }

    private fun contentsEqual(list1: List<Item>, list2: List<Item>): Boolean {
        if (list1.size != list2.size) {
            return true
        }
        val itr1 = list1.iterator()
        val itr2 = list2.iterator()
        while (itr1.hasNext()) {
            if (!contentsEqual(itr1.next(), itr2.next())) return false
        }
        return true
    }

    override fun toString(): String {
        return "Item " + name.get()
    }

    internal fun contentEquals(item: Item): Boolean {
        return (name.get() == item.name.get()
                && contentsEqual(observableChildren, item.observableChildren)
                && contentsEqual(children, item.children)
                && contentsEqual(transformedChildren, item.transformedChildren)
                && contentsEqual(singleChild.get(), item.singleChild.get()))
    }

}