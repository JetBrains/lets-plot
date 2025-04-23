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
        if (prop == Element::transform) {
            breadthFirstTraversal(this).forEach { it.invalidateComputedProp(Element::ctm) }
        }

        if (prop == Element::ctm) {
            breadthFirstTraversal(this).forEach { it.invalidateComputedProp(Element::ctm) }
        }

        if (prop == Element::parent) {
            invalidateHierarchy(this)
        }
    }

    override val localBounds: DoubleRectangle
        get() = children
            .filterNot { it is Container && it.children.isEmpty() }
            .map(Element::localBounds)
            .let(::union)
            ?: DoubleRectangle.XYWH(0, 0, 0, 0)

    override val screenBounds: DoubleRectangle
        get() {
            return children
                .filterNot { it is Container && it.children.isEmpty() }
                .map(Element::screenBounds)
                .let(::union)
                ?: DoubleRectangle.XYWH(ctm.tx, ctm.ty, 0, 0)
        }


    private fun invalidateHierarchy(e: Element) {
        e.invalidateComputedProp(Element::parents)
        e.invalidateComputedProp(Element::ctm)
        breadthFirstTraversal(e).forEach {
            it.invalidateComputedProp(Element::parents)
            it.invalidateComputedProp(Element::ctm)
        }
    }

    protected open fun onChildSet(event: CollectionItemEvent<out Element>) { }
    protected open fun onChildAdded(event: CollectionItemEvent<out Element>) { }
    protected open fun onChildRemoved(event: CollectionItemEvent<out Element>) { }
}
