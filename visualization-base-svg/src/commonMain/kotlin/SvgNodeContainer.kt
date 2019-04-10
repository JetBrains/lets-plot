package jetbrains.datalore.visualization.base.svg

import jetbrains.datalore.base.observable.event.ListenerCaller
import jetbrains.datalore.base.observable.event.Listeners
import jetbrains.datalore.base.observable.property.Property
import jetbrains.datalore.base.observable.property.ValueProperty
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.visualization.base.svg.event.SvgAttributeEvent

class SvgNodeContainer(root: SvgSvgElement) {
    private val mySvgRoot = object : ValueProperty<SvgSvgElement?>(null) {
        fun set(value: SvgSvgElement) {
            if (this.get() != null) {
                this.get()!!.detach()
            }
            super.set(value)
            this.get()!!.attach(this@SvgNodeContainer)
        }
    }
    private val myListeners: Listeners<SvgNodeContainerListener> = Listeners()
    var peer: SvgPlatformPeer? = null

    init {
        mySvgRoot.set(root)
    }

    fun getPeer(): SvgPlatformPeer = peer!!

    fun root(): Property<SvgSvgElement?> {
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