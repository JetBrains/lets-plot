/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.observable.property

import org.jetbrains.letsPlot.commons.intern.function.Supplier
import org.jetbrains.letsPlot.commons.intern.observable.event.EventHandler
import org.jetbrains.letsPlot.commons.registration.Registration

/**
 * Derived property based on a Supplier<Value> and a set of dependencies </Value>
 */
class SimpleDerivedProperty<ValueT>(
    private val mySupplier: Supplier<ValueT>,
    vararg deps: ReadableProperty<*>
) :
    BaseDerivedProperty<ValueT>(mySupplier.get()) {

    private val myDependencies: Array<ReadableProperty<*>>
    private var myRegistrations: Array<Registration>? = null

    override val propExpr: String
        get() {
            val result = StringBuilder()
            result.append("derived(")
            result.append(mySupplier)
            for (d in myDependencies) {
                result.append(", ")
                result.append(d.propExpr)
            }
            result.append(")")
            return result.toString()
        }

    init {

//        myDependencies = arrayOfNulls<ReadableProperty<*>>(deps.size)
//        System.arraycopy(deps, 0, myDependencies, 0, deps.size)
        myDependencies = arrayOf(*deps)
    }

    override fun doAddListeners() {
        myRegistrations = Array(myDependencies.size) { i ->
            register(myDependencies[i])
        }

//        myRegistrations = arrayOfNulls<Registration>(myDependencies.size)
//        myRegistrations = arr
//        var i = 0
//        val myDependenciesLength = myDependencies.size
//        while (i < myDependenciesLength) {
//            myRegistrations[i] = register<Any>(myDependencies[i])
//            i++
//        }
    }

    private fun <DependencyT> register(prop: ReadableProperty<DependencyT>): Registration {
        return prop.addHandler(object : EventHandler<PropertyChangeEvent<out DependencyT>> {
            override fun onEvent(event: PropertyChangeEvent<out DependencyT>) {
                somethingChanged()
            }
        })
    }

    override fun doRemoveListeners() {
        for (r in myRegistrations!!) {
            r.remove()
        }
        myRegistrations = null
    }

    override fun doGet(): ValueT {
        return mySupplier.get()
    }
}