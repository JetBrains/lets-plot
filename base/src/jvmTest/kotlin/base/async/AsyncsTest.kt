/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.async

import jetbrains.datalore.base.async.asyncAssert.AsyncAssert.Companion.assertThat
import jetbrains.datalore.base.function.Supplier
import org.jetbrains.letsPlot.base.intern.async.Async
import org.jetbrains.letsPlot.base.intern.async.Asyncs
import org.jetbrains.letsPlot.base.intern.async.SimpleAsync
import kotlin.test.Test
import kotlin.test.fail

class AsyncsTest {
    @Test
    fun constantAsync() {
        assertThat(Asyncs.constant(239)).succeededWith(239)
    }

    @Test
    fun failureAsync() {
        assertThat(Asyncs.failure<Int>(Throwable())).failed()
    }

    @Test
    fun map() {
        val c = Asyncs.constant(239)
        val mapped = c.map { value -> value + 1 }
        assertThat(mapped).succeededWith(240)
    }

    @Test
    fun mapFailure() {
        val a = Asyncs.constant(1)
        val mapped = a.map { throw RuntimeException("test") }
        assertThat(mapped).failed()
    }

    @Test(expected = IllegalArgumentException::class)
    fun ignoreHandlerException() {
        val async = SimpleAsync<Int>()
        val res = async.map { value -> value + 1 }
        res.onSuccess { throw IllegalArgumentException() }
        res.onFailure { fail() }
        async.success(1)
    }

    @Test
    fun select() {
        val c = Asyncs.constant(239)
        val assertThat = assertThat(c.flatMap { value -> Asyncs.constant(value + 1) })
        assertThat.succeededWith(240)
    }

    @Test
    fun selectException() {
        val a = Asyncs.constant(1)
        assertThat(a.flatMap<Any> { throw RuntimeException("test") }).failureIs(RuntimeException::class.java, "test")
    }

    @Test
    fun selectFirstFailure() {
        val failure = Asyncs.failure<Int>(Throwable())
        assertThat(failure.flatMap { value -> Asyncs.constant(value + 1) }).failed()
    }

    @Test
    fun selectReturnedFailure() {
        val async = Asyncs.constant(1)
        assertThat(async.flatMap { Asyncs.failure<Int>(Throwable()) }).failed()
    }

    @Test
    fun selectReturnsNull() {
        val async = Asyncs.constant(1)
        assertThat(async.flatMap<Int> { null }).succeededWith(null)
    }

    @Test
    fun untilSuccess() {
        assertThat(
                Asyncs.untilSuccess(object : Supplier<Async<Int>> {
                    override fun get(): Async<Int> {
                        return Asyncs.constant(1)
                    }
                }))
                .succeededWith(1)
    }

    @Test
    fun untilSuccessException() {
        assertThat(
                Asyncs.untilSuccess(object : Supplier<Async<Int>> {
                    private var myCounter = 0

                    override fun get(): Async<Int> {
                        myCounter++
                        return if (myCounter < 2) {
                            throw RuntimeException()
                        } else {
                            Asyncs.constant(myCounter)
                        }
                    }
                }))
                .succeededWith(2)
    }

    @Test
    fun untilSuccessWithFailures() {
        assertThat(
                Asyncs.untilSuccess(object : Supplier<Async<Int>> {
                    private var myCounter: Int = 0

                    override fun get(): Async<Int> {
                        return if (myCounter++ < 10) {
                            Asyncs.failure(RuntimeException())
                        } else Asyncs.constant(1)
                    }
                }))
                .succeededWith(1)
    }
}