/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.observable.event


import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.base.registration.throwableHandlers.ThrowableHandlers

/**
 * Reusable container for listeners.
 * It supports:
 * - managing listeners
 * - firing events
 */
open class Listeners<ListenerT> {
    private var myListeners: MutableList<Any>? = null
    private var myFireDepth: Int = 0
    private var myListenersCount: Int = 0

    val isEmpty: Boolean
        get() = myListeners == null || myListeners!!.isEmpty()

    fun add(l: ListenerT): Registration {
        if (isEmpty) {
            beforeFirstAdded()
        }

        if (myFireDepth > 0) {
            myListeners!!.add(ListenerOp(l, true))
        } else {
            if (myListeners == null) {
                myListeners = ArrayList(1)
            }
            myListeners!!.add(l as Any)
            myListenersCount++
        }
        return object : Registration() {
            override fun doRemove() {
                if (myFireDepth > 0) {
                    myListeners!!.add(
                        ListenerOp(
                            l,
                            false
                        )
                    )
                } else {
                    myListeners!!.remove(l as Any)
                    myListenersCount--
                }

                if (isEmpty) {
                    afterLastRemoved()
                }
            }
        }
    }

    fun fire(h: ListenerCaller<ListenerT>) {
        if (isEmpty) return
        beforeFire()
        //exception can be thrown from ThrowableHandlers.getInstance().handle()
        try {
            val size = myListenersCount
            for (i in 0 until size) {
                @Suppress("UNCHECKED_CAST")
                val l = myListeners!![i] as ListenerT

                if (isRemoved(l)) continue

                try {
                    h.call(l)
                } catch (t: Throwable) {
                    ThrowableHandlers.instance.handle(t)
                }

            }
        } finally {
            afterFire()
        }
    }

    private fun isRemoved(l: ListenerT): Boolean {
        val size = myListeners!!.size
        for (i in myListenersCount until size) {
            @Suppress("UNCHECKED_CAST")
            val op = myListeners!![i] as ListenerOp<ListenerT>
            if (!op.add && op.listener === l) return true
        }
        return false
    }

    protected open fun beforeFirstAdded() {}

    protected open fun afterLastRemoved() {}

    private fun beforeFire() {
        myFireDepth++
    }

    private fun afterFire() {
        myFireDepth--
        if (myFireDepth == 0) {
            val opsList = myListeners!!.subList(myListenersCount, myListeners!!.size)
            val ops = opsList.toTypedArray()
            opsList.clear()
            for (o in ops) {
                @Suppress("UNCHECKED_CAST")
                val op = o as ListenerOp<ListenerT>
                if (op.add) {
                    myListeners!!.add(op.listener as Any)
                    myListenersCount++
                } else {
                    myListeners!!.remove(op.listener as Any)
                    myListenersCount--
                }
            }
            if (isEmpty) {
                afterLastRemoved()
            }
        }
    }

    internal fun size(): Int {
        return if (myListeners == null) 0 else myListeners!!.size
    }

    private class ListenerOp<ListenerT> internal constructor(
        val listener: ListenerT,
        val add: Boolean
    )
}
