/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.async

import jetbrains.datalore.base.function.Consumer
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.commons.intern.observable.event.ListenerCaller
import org.jetbrains.letsPlot.commons.intern.observable.event.Listeners

class SimpleAsync<ItemT> : ResolvableAsync<ItemT> {
    private var mySuccessItem: ItemT? = null
    private var mySucceeded = false

    private var myFailureThrowable: Throwable? = null
    private var myFailed = false

    private var mySuccessHandlers: Listeners<Consumer<in ItemT>>? = Listeners()
    private var myFailureHandlers: Listeners<Consumer<Throwable>>? = Listeners()

    override fun onSuccess(successHandler: Consumer<in ItemT>): Registration {

        if (alreadyHandled()) {
            if (mySucceeded) {
                successHandler(mySuccessItem!!)
            }
            return Registration.EMPTY
        }
        return mySuccessHandlers!!.add(successHandler)
    }

    override fun onResult(successHandler: Consumer<in ItemT>, failureHandler: Consumer<Throwable>): Registration {
        val successRegistration = onSuccess(successHandler)
        val failureRegistration = onFailure(failureHandler)
        return object : Registration() {
            override fun doRemove() {
                successRegistration.remove()
                failureRegistration.remove()
            }
        }
    }

    override fun onFailure(failureHandler: Consumer<Throwable>): Registration {
        if (alreadyHandled()) {
            if (myFailed) {
                failureHandler(myFailureThrowable!!)
            }
            return Registration.EMPTY
        }
        return myFailureHandlers!!.add(failureHandler)
    }

    override fun <ResultT> map(success: (ItemT) -> ResultT): Async<ResultT> {
        return Asyncs.map(this, success, SimpleAsync())
    }

    override fun <ResultT> flatMap(success: (ItemT) -> Async<ResultT>?): Async<ResultT?> {
        return Asyncs.select<ItemT, ResultT>(this, success, SimpleAsync())
    }

    override fun success(result: ItemT) {
        if (alreadyHandled()) {
            throw IllegalStateException("Async already completed")
        }
        mySuccessItem = result
        mySucceeded = true

        mySuccessHandlers!!.fire(object : ListenerCaller<Consumer<in ItemT>> {
            override fun call(l: Consumer<in ItemT>) {
                @Suppress("UNCHECKED_CAST")
                l(mySuccessItem as ItemT)
            }
        })
        clearHandlers()
    }

    override fun failure(t: Throwable) {
        if (alreadyHandled()) {
            throw IllegalStateException("Async already completed")
        }
        myFailureThrowable = t
        myFailed = true

        myFailureHandlers!!.fire(object : ListenerCaller<Consumer<Throwable>> {
            override fun call(l: Consumer<Throwable>) {
                l(myFailureThrowable!!)
            }
        })
        clearHandlers()
    }

    private fun clearHandlers() {
        mySuccessHandlers = null
        myFailureHandlers = null
    }

    private fun alreadyHandled(): Boolean {
        return mySucceeded || myFailed
    }

    internal fun hasSucceeded(): Boolean {
        return mySucceeded
    }

    internal fun hasFailed(): Boolean {
        return myFailed
    }
}