/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.edt

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.async.Asyncs.constant
import jetbrains.datalore.base.async.asyncAssert.AsyncMatchers
import jetbrains.datalore.base.async.asyncAssert.AsyncMatchers.failed
import jetbrains.datalore.base.async.asyncAssert.AsyncMatchers.result
import jetbrains.datalore.base.edt.RunnableWithAsync.Companion.fromAsyncSupplier
import jetbrains.datalore.base.edt.RunnableWithAsync.Companion.fromRunnable
import jetbrains.datalore.base.edt.RunnableWithAsync.Companion.fromSupplier
import jetbrains.datalore.base.function.Runnable
import jetbrains.datalore.base.function.Supplier
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

class RunnableWithAsyncTest {

    private var invoked: Boolean = false

    private val runnable: Runnable
        get() = object : Runnable {
            override fun run() {
                invoked = true
            }
        }

    private val failingRunnable: Runnable
        get() = object : Runnable {
            override fun run() {
                invoked = true
                throw RuntimeException()
            }
        }

    @BeforeTest
    fun setup() {
        invoked = false
    }

    @AfterTest
    fun after() {
        assertTrue(invoked)
    }

    @Test
    fun successFromRunnable() {
        val async = runAndReturnAsync(fromRunnable(runnable))
        assertThat(async, AsyncMatchers.succeeded())
    }

    @Test
    fun failFromRunnable() {
        runAndAssertError(fromRunnable(failingRunnable))
    }

    @Test
    fun successFromPlainSupplier() {
        val async = runAndReturnAsync(fromSupplier(getSupplier(VALUE)))
        assertThat(async, result(`is`(VALUE)))
    }

    @Test
    fun failFromPlainSupplier() {
        runAndAssertError(fromSupplier(this.getFailingSupplier<Int>()))
    }

    @Test
    fun successFromAsyncSupplier() {
        val async = runAndReturnAsync(fromAsyncSupplier(getSupplier(constant(VALUE))))
        assertThat(async, result(`is`(VALUE)))
    }

    @Test
    fun failFromAsyncSupplier() {
        runAndAssertError(fromAsyncSupplier(this.getFailingSupplier<Async<Int>>()))
    }

    private fun <T> runAndReturnAsync(runnableWithAsync: RunnableWithAsync<T>): Async<T> {
        runnableWithAsync.run()
        return runnableWithAsync
    }

    private fun <T> runAndAssertError(runnableWithAsync: RunnableWithAsync<T>) {
        var caught = false
        try {
            runnableWithAsync.run()
        } catch (ignored: RuntimeException) {
            caught = true
        }

        assertTrue(caught)
        assertThat(runnableWithAsync, failed())
    }

    private fun <ResultT> getSupplier(result: ResultT): Supplier<ResultT> {
        return object : Supplier<ResultT> {
            override fun get(): ResultT {
                invoked = true
                return result
            }
        }
    }

    private fun <ResultT> getFailingSupplier(): Supplier<ResultT> {
        return object : Supplier<ResultT> {
            override fun get(): ResultT {
                invoked = true
                throw RuntimeException()
            }
        }
    }

    companion object {

        private const val VALUE = 42
    }
}
