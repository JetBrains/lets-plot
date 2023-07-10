/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.observable.property

import org.jetbrains.letsPlot.commons.intern.observable.event.EventHandler
import jetbrains.datalore.base.registration.CompositeRegistration
import jetbrains.datalore.base.registration.Registration

/**
 * One and two-way property binding support
 */
object PropertyBinding {
    fun <ValueT> bindOneWay(
        source: ReadableProperty<out ValueT>, target: WritableProperty<in ValueT>
    ): Registration {
        target.set(source.get())
        return source.addHandler(object : EventHandler<PropertyChangeEvent<out ValueT>> {
            override fun onEvent(event: PropertyChangeEvent<out ValueT>) {
                @Suppress("UNCHECKED_CAST")
                target.set(event.newValue as ValueT)
            }
        })
    }

    fun <ValueT> bindTwoWay(source: Property<ValueT>, target: Property<ValueT>): Registration {
        val syncing = ValueProperty<Boolean>(false)
        target.set(source.get())

        class UpdatingEventHandler(private val myForward: Boolean) : EventHandler<PropertyChangeEvent<out ValueT>> {

            override fun onEvent(event: PropertyChangeEvent<out ValueT>) {
                if (syncing.get()) return

                syncing.set(true)
                try {
                    if (myForward) {
                        target.set(source.get())
                    } else {
                        source.set(target.get())
                    }
                } finally {
                    syncing.set(false)
                }
            }
        }

        return CompositeRegistration(
            source.addHandler(UpdatingEventHandler(true)),
            target.addHandler(UpdatingEventHandler(false))
        )
    }
}