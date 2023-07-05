/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.base.intern.async

import jetbrains.datalore.base.function.Consumer
import jetbrains.datalore.base.function.Value
import jetbrains.datalore.base.registration.Registration
import kotlin.test.Test

class SimpleAsyncRegistrationsTest {

    private val async = SimpleAsync<Unit>()

    @Test
    fun removeSuccessRegistration() {
        val reg = async.onSuccess(throwingHandler())
        reg.remove()
        async.success(Unit)
    }

    @Test
    fun removeFailureRegistration() {
        val reg = async.onFailure(throwingFailureHandler())
        reg.remove()
        async.failure(RuntimeException())
    }

    @Test
    fun removeCompositeRegistration1() {
        val reg = async.onResult(throwingHandler<Unit>(), throwingFailureHandler())
        reg.remove()
        async.success(Unit)
    }

    @Test
    fun removeCompositeRegistration2() {
        val reg = async.onResult(throwingHandler<Unit>(), throwingFailureHandler())
        reg.remove()
        async.failure(RuntimeException())
    }

    @Test
    fun removeRegistrationInSuccessHandler() {
        val regValue = Value(Registration.EMPTY)
        val reg = async.onSuccess { regValue.get().remove() }
        regValue.set(reg)
        async.success(Unit)
    }

    @Test
    fun removeRegistrationInFailureHandler() {
        val regValue = Value(Registration.EMPTY)
        val reg = async.onFailure { regValue.get().remove() }
        regValue.set(reg)
        async.failure(RuntimeException())
    }

    @Test
    fun addSuccessHandlerAfterFailure() {
        async.failure(Throwable())
        val reg = async.onSuccess {}
        reg.remove()
    }

    @Test
    fun addFailureHandlerAfterSuccess() {
        async.success(Unit)
        val reg = async.onFailure {}
        reg.remove()
    }

    @Test
    fun removeSuccessRegistrationAfterSuccess() {
        val reg = async.onSuccess {}
        async.success(Unit)
        reg.remove()
    }

    @Test
    fun removeSuccessRegistrationAfterFailure() {
        val reg = async.onSuccess {}
        async.failure(RuntimeException())
        reg.remove()
    }

    @Test
    fun removeFailureRegistrationAfterSuccess() {
        val reg = async.onFailure {}
        async.success(Unit)
        reg.remove()
    }

    @Test
    fun removeFailureRegistrationAfterFailure() {
        val reg = async.onFailure {}
        async.failure(RuntimeException())
        reg.remove()
    }

    private fun throwingFailureHandler(): Consumer<Throwable> {
        return throwingHandler()
    }

    private fun <ItemT> throwingHandler(): Consumer<ItemT> {
        return { throw RuntimeException() }
    }
}