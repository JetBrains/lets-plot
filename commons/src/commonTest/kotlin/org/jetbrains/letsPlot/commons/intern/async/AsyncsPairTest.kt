/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.async

import org.jetbrains.letsPlot.commons.registration.Registration
import kotlin.test.*

class AsyncsPairTest {

    private val first = SimpleAsync<Int?>()
    private val second = SimpleAsync<String?>()

    private var initReg: Registration? = null
    private var result: Pair<Int?, String?>? = null
    private var error: Throwable? = null

    @BeforeTest
    fun init() {
        initPair(first, second)
    }

    private fun initPair(first: Async<Int?>, second: Async<String?>) {
        val pair = Asyncs.pair(first, second)
        initReg = pair.onResult(
            { item ->
                result = item
            },
            { throwable ->
                error = throwable
            })
    }

    @Test
    fun successFirstEarlier() {
        first.success(1)

        assertNull(result)

        second.success("a")

        assertSucceeded()
    }

    @Test
    fun successSecondEarlier() {
        second.success("a")

        assertNull(result)

        first.success(1)

        assertSucceeded()
    }

    @Test
    fun successWithNulls() {
        first.success(null)
        second.success(null)

        assertNotNull(result)
        assertNull(result!!.first)
        assertNull(result!!.second)
    }

    @Test
    fun successFirstThenFailure() {
        first.success(1)
        val throwable = Throwable()
        second.failure(throwable)

        assertSame(throwable, error)
    }

    @Test
    fun successSecondThenFailure() {
        second.success("a")
        val throwable = Throwable()
        first.failure(throwable)

        assertSame(throwable, error)
    }

    @Test
    fun failureFirstThenSuccessSecond() {
        first.failure(Throwable())
        second.success("a")

        assertNotNull(error)
    }

    @Test
    fun failureSecondThenSuccessFirst() {
        second.failure(Throwable())
        first.success(1)

        assertNotNull(error)
    }

    @Test
    fun doubleFailureFirstEarlier() {
        first.failure(Throwable())
        second.failure(Throwable())

        assertNotNull(error)
    }

    @Test
    fun doubleFailureSecondEarlier() {
        second.failure(Throwable())
        first.failure(Throwable())

        assertNotNull(error)
    }

    @Test
    fun successThenFirstAlreadySucceeded() {
        initReg!!.remove()
        initPair(Asyncs.constant(1), second)

        second.success("a")

        assertSucceeded()
    }

    @Test
    fun failureThenFirstAlreadySucceeded() {
        initReg!!.remove()
        initPair(Asyncs.constant(1), second)

        second.failure(Throwable())

        assertNotNull(error)
    }

    @Test
    fun successThenSecondAlreadySucceeded() {
        initReg!!.remove()
        initPair(first, Asyncs.constant("a"))

        first.success(1)

        assertSucceeded()
    }

    @Test
    fun failureThenSecondAlreadySucceeded() {
        initReg!!.remove()
        initPair(first, Asyncs.constant("a"))

        first.failure(Throwable())

        assertNotNull(error)
    }


    @Test
    fun successSecondThenFirstAlreadyFailed() {
        initReg!!.remove()
        initPair(Asyncs.failure(Throwable()), second)

        second.success("a")

        assertNotNull(error)
    }

    @Test
    fun failureSecondThenFirstAlreadyFailed() {
        initReg!!.remove()
        initPair(Asyncs.failure(Throwable()), second)

        second.failure(Throwable())

        assertNotNull(error)
    }

    @Test
    fun successFirstThenSecondAlreadyFailed() {
        initReg!!.remove()
        initPair(first, Asyncs.failure(Throwable()))

        first.success(1)

        assertNotNull(error)
    }

    @Test
    fun failureFirstThenSecondAlreadyFailed() {
        initReg!!.remove()
        initPair(first, Asyncs.failure(Throwable()))

        first.failure(Throwable())

        assertNotNull(error)
    }

    @Test
    fun bothSucceeded() {
        initReg!!.remove()
        initPair(Asyncs.constant(1), Asyncs.constant("a"))

        assertSucceeded()
    }

    @Test
    fun firstSucceededSecondFailed() {
        initReg!!.remove()
        initPair(Asyncs.constant(1), Asyncs.failure(Throwable()))

        assertNotNull(error)
    }

    @Test
    fun firstFailedSecondSucceeded() {
        initReg!!.remove()
        initPair(Asyncs.failure(Throwable()), Asyncs.constant("a"))

        assertNotNull(error)
    }

    @Test
    fun bothFailed() {
        initReg!!.remove()
        initPair(Asyncs.failure(Throwable()), Asyncs.failure(Throwable()))

        assertNotNull(error)
    }

    private fun assertSucceeded() {
        assertNotNull(result)
        assertEquals(1, result!!.first)
        assertEquals("a", result!!.second)
    }
}
