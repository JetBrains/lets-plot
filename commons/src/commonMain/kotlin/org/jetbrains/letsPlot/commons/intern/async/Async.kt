/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.async

import jetbrains.datalore.base.function.Consumer
import org.jetbrains.letsPlot.commons.registration.Registration

/**
 * Asynchronous computation
 * You must eventually call either succeedHandler or failureHandler.
 * If you imply a condition on handlers calls (i.e. synchronization)
 * you should imply the same conditions on map/flatMap handlers, and vice versa.
 *
 * Users aren't required to call [Registration.remove] for registrations returned by handle methods.
 * Implementations should make appropriate cleanup to avoid memory leaks when async is succeeded or failed.
 */
interface Async<ItemT> {
    fun onSuccess(successHandler: Consumer<in ItemT>): Registration
    fun onResult(successHandler: Consumer<in ItemT>, failureHandler: Consumer<Throwable>): Registration
    fun onFailure(failureHandler: Consumer<Throwable>): Registration

    /**
     * This method must always create new async every time it's called.
     * Every error thrown in `success` should fail async with corresponding `Throwable`
     */
    fun <ResultT> map(success: (ItemT) -> ResultT): Async<ResultT>

    /**
     * Should comply with A+ promise 'then' method except it has no failure handler.
     * See [A+ promise spec](https://promisesaplus.com/) for more detail.
     * This method must always create new async every time it's called.
     * Every error thrown in `success` should fail async with corresponding `Throwable`
     */
    fun <ResultT> flatMap(success: (ItemT) -> Async<ResultT>?): Async<ResultT?>
}