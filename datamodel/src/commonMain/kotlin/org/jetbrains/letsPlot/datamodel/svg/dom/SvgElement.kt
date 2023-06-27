/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.svg.dom

import jetbrains.datalore.base.event.Event
import jetbrains.datalore.base.listMap.ListMap
import jetbrains.datalore.base.observable.event.EventHandler
import jetbrains.datalore.base.observable.event.ListenerCaller
import jetbrains.datalore.base.observable.event.Listeners
import jetbrains.datalore.base.observable.property.Property
import jetbrains.datalore.base.observable.property.PropertyChangeEvent
import jetbrains.datalore.base.observable.property.ReadableProperty
import jetbrains.datalore.base.registration.Registration
import org.jetbrains.letsPlot.datamodel.svg.event.SvgAttributeEvent
import org.jetbrains.letsPlot.datamodel.svg.event.SvgEventHandler
import org.jetbrains.letsPlot.datamodel.svg.event.SvgEventSpec

abstract class SvgElement : SvgNode() {

    companion object {
        val ID: SvgAttributeSpec<String> =
            SvgAttributeSpec.createSpec("id")
    }

    abstract val elementName: String

    private val myAttributes = AttributeMap()
    private var myListeners: Listeners<SvgElementListener>? = null
    private val myEventPeer = SvgEventPeer()

    val ownerSvgElement: SvgSvgElement?
        get() {
            var cur: SvgNode? = this
            while (cur != null && cur !is SvgSvgElement) {
                cur = cur.parent().get()
            }
            return if (cur != null) {
                cur as SvgSvgElement?
            } else null
        }

    val attributeKeys: Set<SvgAttributeSpec<*>>
        get() = myAttributes.keySet()

    fun id(): Property<String?> {
        return getAttribute(ID)
    }

    fun handlersSet(): ReadableProperty<Set<SvgEventSpec>> {
        return myEventPeer.handlersSet()
    }

    fun <EventT : Event> addEventHandler(spec: SvgEventSpec, handler: SvgEventHandler<EventT>): Registration {
        return myEventPeer.addEventHandler(spec, handler)
    }

    fun <EventT : Event> dispatch(spec: SvgEventSpec, event: EventT) {
        myEventPeer.dispatch(spec, event, this)

        if (parent().get() != null && !event.isConsumed && parent().get() is SvgElement) {
            (parent().get() as SvgElement).dispatch(spec, event)
        }
    }

    private fun getSpecByName(name: String): SvgAttributeSpec<Any> {
        return SvgAttributeSpec.createSpec(name)
    }

    fun <ValueT> getAttribute(spec: SvgAttributeSpec<ValueT>): Property<ValueT?> {
        return object : Property<ValueT?> {
            override val propExpr: String
                get() = "$this.$spec"

            override fun get(): ValueT? {
                return myAttributes[spec]
            }

            override fun set(value: ValueT?) {
                myAttributes[spec] = value
            }

            override fun addHandler(handler: EventHandler<PropertyChangeEvent<out ValueT?>>): Registration {
                return addListener(object : SvgElementListener {
                    override fun onAttrSet(event: SvgAttributeEvent<*>) {
                        if (spec !== event.attrSpec) {
                            return
                        }
                        @Suppress("UNCHECKED_CAST")
                        val oldValue = event.oldValue as ValueT?
                        @Suppress("UNCHECKED_CAST")
                        val newValue = event.newValue as ValueT?
                        handler.onEvent(PropertyChangeEvent(oldValue, newValue))

                    }
                })
            }
        }
    }

    fun getAttribute(name: String): Property<Any?> {
        val spec = getSpecByName(name)
        return getAttribute(spec)
    }

    fun <ValueT> setAttribute(spec: SvgAttributeSpec<ValueT>, value: ValueT) {
        getAttribute(spec).set(value)
    }

    // if attr is one of pre-defined typed attrs (like CX in ellipse), the behaviour of this method is undefined
    fun setAttribute(name: String, value: String?) {
        getAttribute(name).set(value)
    }

    private fun onAttributeChanged(event: SvgAttributeEvent<*>) {
        if (myListeners != null) {
            myListeners!!.fire(object : ListenerCaller<SvgElementListener> {
                override fun call(l: SvgElementListener) {
                    l.onAttrSet(event)
                }
            })
        }

        if (isAttached()) {
            container().attributeChanged(this, event)
        }
    }

    fun addListener(l: SvgElementListener): Registration {
        if (myListeners == null) {
            myListeners = Listeners()
        }
        val reg = myListeners!!.add(l)
        return object : Registration() {
            override fun doRemove() {
                reg.remove()
                if (myListeners!!.isEmpty) {
                    myListeners = null
                }
            }
        }
    }

    override fun toString(): String {
        return "<$elementName ${myAttributes.toSvgString()}></$elementName>"
    }

    private inner class AttributeMap {
        private var myAttrs: ListMap<SvgAttributeSpec<*>, Any>? = null

        val isEmpty: Boolean
            get() = myAttrs == null || myAttrs!!.isEmpty

        fun size(): Int {
            return if (myAttrs == null) 0 else myAttrs!!.size()
        }

        fun containsKey(key: SvgAttributeSpec<*>): Boolean {
            return myAttrs != null && myAttrs!!.containsKey(key)
        }

        operator fun <ValueT> get(spec: SvgAttributeSpec<ValueT>): ValueT? {
            return if (myAttrs != null && myAttrs!!.containsKey(spec)) {
                @Suppress("UNCHECKED_CAST")
                myAttrs!![spec] as ValueT
            } else null
        }

        operator fun <ValueT> set(spec: SvgAttributeSpec<ValueT>, value: ValueT?): ValueT? {
            if (myAttrs == null) {
                myAttrs = ListMap()
            }

            val oldValue = if (value == null) {
                @Suppress("UNCHECKED_CAST")
                val v = myAttrs!!.remove(spec) as ValueT
                v
            } else {
                @Suppress("UNCHECKED_CAST")
                val v = myAttrs!!.put(spec, value) as ValueT
                v
            }

            if (value != oldValue) {
                val event = SvgAttributeEvent(spec, oldValue, value)
                this@SvgElement.onAttributeChanged(event)
            }

            return oldValue
        }

        fun <ValueT> remove(spec: SvgAttributeSpec<ValueT>): ValueT? {
            return set(spec, null)
        }

        fun keySet(): Set<SvgAttributeSpec<*>> {
            return if (myAttrs == null) {
                emptySet()
            } else {
                val keySet = myAttrs!!.keySet()
                keySet
            }
        }

        internal fun toSvgString(): String {
            val builder = StringBuilder()
            for (spec in keySet()) {
                builder.append(spec.name)
                        .append("=\"")
                        .append(get(spec))
                        .append("\" ")
            }
            return builder.toString()
        }

        override fun toString(): String {
            return toSvgString()
        }
    }
}
