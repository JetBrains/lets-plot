/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.base.intern.async

import jetbrains.datalore.base.function.Consumer
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class SimpleAsyncTest {

    private val async = SimpleAsync<Unit>()

    @Test
    fun exceptionInSuccessHandler() {
        async.onSuccess(throwingHandler())
        try {
            async.success(Unit)
        } catch (ignored: RuntimeException) {
        }

        assertTrue(async.hasSucceeded())
    }

    @Test
    fun exceptionInFailureHandler() {
        async.onFailure(this.throwingHandler())
        try {
            async.failure(Throwable())
        } catch (ignored: RuntimeException) {
        }

        assertTrue(async.hasFailed())
    }

    @Test
    fun callSuccessInSuccessHandler() {
        assertFailsWith(IllegalStateException::class) {
            async.onSuccess(succeedingHandler(async))
            async.success(Unit)
        }
        println("I:  ${async.hasSucceeded()}")
    }

    @Test
    fun callSuccessInFailureHandler() {
        println("II: ${async.hasSucceeded()}")
        assertFailsWith(IllegalStateException::class) {
            async.onFailure(this.succeedingHandler(async))
            async.failure(Throwable())
        }
    }

    @Test
    fun callFailureInSuccessHandler() {
        assertFailsWith(IllegalStateException::class) {
            async.onSuccess(failingHandler(async))
            async.success(Unit)
        }
    }

    @Test
    fun callFailureInFailureHandler() {
        assertFailsWith(IllegalStateException::class) {
            async.onFailure(this.failingHandler(async))
            async.failure(Throwable())
        }
    }

    private fun <ResultT> throwingHandler(): Consumer<ResultT> {
        return { throw RuntimeException() }
    }

    private fun <ResultT> succeedingHandler(async: SimpleAsync<Unit>): Consumer<ResultT> {
        return { async.success(Unit) }
    }

    private fun <ResultT> failingHandler(async: SimpleAsync<Unit>): Consumer<ResultT> {
        return { async.failure(Throwable()) }
    }
}
