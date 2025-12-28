/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.shape

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent
import org.jetbrains.letsPlot.commons.intern.observable.collections.list.ObservableArrayList
import org.jetbrains.letsPlot.commons.intern.observable.collections.list.ObservableList
import org.jetbrains.letsPlot.commons.intern.observable.event.EventHandler
import kotlin.reflect.KProperty

internal abstract class Container : Element() {
    val children: ObservableList<Element> = ObservableArrayList()

    init {
        children.addHandler(object : EventHandler<CollectionItemEvent<out Element>> {
            override fun onEvent(event: CollectionItemEvent<out Element>) {
                when (event.type) {
                    CollectionItemEvent.EventType.ADD -> {
                        event.newItem?.let { it.parent = this@Container }
                        onChildAdded(event)
                    }
                    CollectionItemEvent.EventType.REMOVE -> {
                        event.oldItem?.let { it.parent = null }
                        onChildRemoved(event)
                    }
                    CollectionItemEvent.EventType.SET -> {
                        event.oldItem?.let { it.parent = null }
                        event.newItem?.let { it.parent = this@Container }
                        onChildSet(event)
                    }
                }
            }
        })
    }

    override fun onPropertyChanged(prop: KProperty<*>) {
        super.onPropertyChanged(prop)
        if (prop == Element::parent) {
            markDirty()
        }
    }

    override fun calculateLocalBBox(): DoubleRectangle {
        return children
            .filterNot { it is Container && it.children.isEmpty() }
            .map { child ->
                // Transform child's local box into this container's space
                // child.transform is fast, child.bBoxLocal is cached.
                child.transform.transform(child.bBoxLocal)
            }
            .let(::union)
            ?: DoubleRectangle.ZERO
    }

    protected open fun onChildSet(event: CollectionItemEvent<out Element>) {
        markDirty()
        invalidateGeometry()
    }

    protected open fun onChildAdded(event: CollectionItemEvent<out Element>) {
        markDirty()
        invalidateGeometry()
    }

    protected open fun onChildRemoved(event: CollectionItemEvent<out Element>) {
        markDirty()
        invalidateGeometry()
    }
}