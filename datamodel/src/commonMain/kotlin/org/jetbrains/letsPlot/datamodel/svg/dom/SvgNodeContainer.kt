/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.svg.dom

import org.jetbrains.letsPlot.commons.intern.observable.event.ListenerCaller
import org.jetbrains.letsPlot.commons.intern.observable.event.Listeners
import org.jetbrains.letsPlot.commons.intern.observable.property.Property
import org.jetbrains.letsPlot.commons.intern.observable.property.ValueProperty
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.datamodel.svg.event.SvgAttributeEvent

class SvgNodeContainer(root: SvgSvgElement) {
    private val mySvgRoot: ValueProperty<SvgSvgElement> = object : ValueProperty<SvgSvgElement>(root) {
        override fun set(value: SvgSvgElement) {
            this.get().detach()
            super.set(value)
            value.attach(this@SvgNodeContainer)
        }
    }
    private val myListeners: Listeners<SvgNodeContainerListener> = Listeners()
    private var myPeer: SvgPlatformPeer? = null

    init {
        mySvgRoot.get().attach(this)
    }

    fun setPeer(peer: SvgPlatformPeer?) {
        myPeer = peer
    }

    fun getPeer(): SvgPlatformPeer? = myPeer

    fun root(): Property<SvgSvgElement> {
        return mySvgRoot
    }

    fun addListener(l: SvgNodeContainerListener): Registration {
        return myListeners.add(l)
    }

    internal fun attributeChanged(element: SvgElement, event: SvgAttributeEvent<*>) {
        myListeners.fire(object : ListenerCaller<SvgNodeContainerListener> {
            override fun call(l: SvgNodeContainerListener) {
                l.onAttributeSet(element, event)
            }
        })
    }

    internal fun svgNodeAttached(node: SvgNode) {
        myListeners.fire(object : ListenerCaller<SvgNodeContainerListener> {
            override fun call(l: SvgNodeContainerListener) {
                l.onNodeAttached(node)
            }
        })
    }

    internal fun svgNodeDetached(node: SvgNode) {
        myListeners.fire(object : ListenerCaller<SvgNodeContainerListener> {
            override fun call(l: SvgNodeContainerListener) {
                l.onNodeDetached(node)
            }
        })
    }
}