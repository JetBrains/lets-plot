/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.scene

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent
import org.jetbrains.letsPlot.commons.intern.observable.collections.list.ObservableArrayList
import org.jetbrains.letsPlot.commons.intern.observable.collections.list.ObservableList
import org.jetbrains.letsPlot.commons.intern.observable.event.EventHandler

internal abstract class Container : Node() {
    val children: ObservableList<Node> = ObservableArrayList()

    init {
        children.addHandler(object : EventHandler<CollectionItemEvent<out Node>> {
            override fun onEvent(event: CollectionItemEvent<out Node>) {
                when (event.type) {
                    CollectionItemEvent.EventType.ADD -> {
                        event.newItem?.parent = this@Container
                        onChildAdded(event)
                    }
                    CollectionItemEvent.EventType.REMOVE -> {
                        event.oldItem?.parent = null
                        onChildRemoved(event)
                    }
                    CollectionItemEvent.EventType.SET -> {
                        event.oldItem?.parent = null
                        event.newItem?.parent = this@Container
                        onChildSet(event)
                    }
                }
            }
        })
    }

    override fun onAttributeChanged(attrSpec: AttributeSpec) {
        super.onAttributeChanged(attrSpec)
        if (attrSpec == ParentAttrSpec) {
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

    protected open fun onChildSet(event: CollectionItemEvent<out Node>) {
        markDirty()
        invalidateGeometry()
    }

    protected open fun onChildAdded(event: CollectionItemEvent<out Node>) {
        markDirty()
        invalidateGeometry()
    }

    protected open fun onChildRemoved(event: CollectionItemEvent<out Node>) {
        markDirty()
        invalidateGeometry()
    }
}