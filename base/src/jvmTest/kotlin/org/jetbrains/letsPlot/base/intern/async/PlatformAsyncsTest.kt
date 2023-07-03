/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.org.jetbrains.letsPlot.base.intern.async

import jetbrains.datalore.org.jetbrains.letsPlot.base.intern.async.asyncAssert.AsyncAssert.Companion.assertThat
import org.jetbrains.letsPlot.base.intern.async.Asyncs
import org.jetbrains.letsPlot.base.intern.async.PlatformAsyncs
import org.jetbrains.letsPlot.base.intern.async.SimpleAsync
import org.jetbrains.letsPlot.base.intern.async.ThrowableCollectionException
import kotlin.test.Test


class PlatformAsyncsTest {
    @Test
    fun parallelSuccess() {
        val parallel = PlatformAsyncs.parallel(Asyncs.constant(1), Asyncs.constant(2))
        assertThat(parallel).succeeded()
    }

    @Test
    fun parallelFailure() {
        val exception = RuntimeException()
        assertThat(
            PlatformAsyncs.parallel(
                Asyncs.constant(1),
                Asyncs.failure<Int>(exception)))
                .failure().isSameAs(exception)
    }

    @Test
    fun parallelFailureMultipleExceptions() {
        assertThat(
                PlatformAsyncs.parallel(
                        Asyncs.constant(1),
                        Asyncs.failure<Int>(Throwable()),
                        Asyncs.failure<Int>(RuntimeException())))
                .failureIs(ThrowableCollectionException::class.java)
    }

    @Test
    fun parallelAlwaysSucceed() {
        assertThat(
                PlatformAsyncs.parallel(
                        listOf(
                                Asyncs.constant(1),
                                Asyncs.failure(Throwable())),
                        true))
                .succeeded()
    }

    @Test
    fun emptyParallel() {
        assertThat(PlatformAsyncs.parallel()).succeeded()
    }

    @Test
    fun parallelResult() {
        assertThat(
                PlatformAsyncs.parallelResult(listOf(
                        Asyncs.constant(1),
                        Asyncs.failure(Throwable()),
                        Asyncs.constant(2))))
                .succeededWith(listOf(1, 2))
    }

    @Test
    fun parallelResultOrder() {
        val first = SimpleAsync<Int>()
        val second = SimpleAsync<Int>()
        val async = PlatformAsyncs.parallelResult(listOf(first, second))
        second.success(2)
        first.success(1)
        assertThat(async).succeededWith(listOf(1, 2))
    }
}
