/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.async.asyncAssert

import org.hamcrest.*
import org.hamcrest.core.IsAnything
import org.jetbrains.letsPlot.commons.intern.async.Async
import org.jetbrains.letsPlot.commons.intern.async.asyncAssert.AsyncResult.Companion.getResult

@Suppress("MemberVisibilityCanBePrivate")
object AsyncMatchers {
    fun <T> result(valueMatcher: Matcher<in T>): Matcher<Async<T>> {
        return object : TypeSafeDiagnosingMatcher<Async<T>>() {
            override fun matchesSafely(item: Async<T>, mismatchDescription: Description): Boolean {
                val result = getResult(item)
                when (result.state) {
                    AsyncResult.AsyncState.SUCCEEDED -> if (valueMatcher.matches(result.value)) {
                        return true
                    } else {
                        mismatchDescription.appendText("result ")
                        valueMatcher.describeMismatch(result.value, mismatchDescription)
                        return false
                    }

                    AsyncResult.AsyncState.FAILED -> {
                        mismatchDescription.appendText("failed with exception: ")
                        mismatchDescription.appendValue(result.error)
                        return false
                    }

                    AsyncResult.AsyncState.UNFINISHED -> {
                        mismatchDescription.appendText("isn't finished yet")
                        return false
                    }
                }
            }

            override fun describeTo(description: Description) {
                description.appendText("a successful async which result ").appendDescriptionOf(valueMatcher)
            }
        }
    }

    fun <E : Throwable> failure(failureMatcher: Matcher<in E>): Matcher<Async<*>> {
        return object : TypeSafeDiagnosingMatcher<Async<*>>() {
            override fun matchesSafely(item: Async<*>, mismatchDescription: Description): Boolean {
                val result = getResult(item)
                return when (result.state) {
                    AsyncResult.AsyncState.SUCCEEDED -> {
                        mismatchDescription.appendText("was a successful async with value: ")
                        mismatchDescription.appendValue(result.value)
                        false
                    }

                    AsyncResult.AsyncState.FAILED -> if (failureMatcher.matches(result.error)) {
                        true
                    } else {
                        mismatchDescription.appendText("failure ")
                        failureMatcher.describeMismatch(result.error, mismatchDescription)
                        false
                    }

                    AsyncResult.AsyncState.UNFINISHED -> {
                        mismatchDescription.appendText("isn't finished yet")
                        false
                    }
                }
            }

            override fun describeTo(description: Description) {
                description.appendText("a failed async which failure ").appendDescriptionOf(failureMatcher)
            }
        }
    }

    fun <T> unfinished(): Matcher<in Async<List<Int>>?>? {
        return object : TypeSafeDiagnosingMatcher<Async<*>>() {
            override fun matchesSafely(item: Async<*>, mismatchDescription: Description): Boolean {
                val result = getResult(item)
                return when (result.state) {
                    AsyncResult.AsyncState.SUCCEEDED -> {
                        mismatchDescription.appendText("async succeeded")
                        false
                    }

                    AsyncResult.AsyncState.FAILED -> {
                        mismatchDescription.appendText("async failed")
                        false
                    }

                    AsyncResult.AsyncState.UNFINISHED -> true
                }
            }

            override fun describeTo(description: Description) {
                description.appendText("an unfinished async")
            }
        }
    }

    fun <T> resultEquals(value: T): Matcher<Async<T>> {
        return result(Matchers.equalTo(value))
    }

    fun <T> succeeded(): Matcher<Async<T>> {
        return result(IsAnything())
    }

    fun voidSuccess(): Matcher<Async<Void>> {
        return result(Matchers.nullValue())
    }

    fun <E : Throwable> failureIs(failureClass: Class<E>): Matcher<Async<*>> {
        return failure(Matchers.isA(failureClass))
    }

    fun <E : Throwable> failureIs(failureClass: Class<E>, message: String): Matcher<Async<*>> {
        return failure(object : BaseMatcher<Throwable>() {
            override fun matches(o: Any): Boolean {
                return classMatches(o) && messageMatches((o as Throwable).message)
            }

            private fun classMatches(o: Any?): Boolean {
                return o != null && o::class.java == failureClass
            }

            private fun messageMatches(actual: String?): Boolean {
                return actual != null && actual.startsWith(message)
            }

            override fun describeTo(description: Description) {
                description.appendText("<" + failureClass.name + ": " + message + '>'.toString())
            }
        })
    }

    fun failed(): Matcher<Async<*>> {
        return failure(IsAnything<Any>())
    }
}
