/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 *
 * This file has been modified by JetBrains : Java code has been converted to Kotlin code.
 *
 * THE FOLLOWING IS THE COPYRIGHT OF THE ORIGINAL DOCUMENT:
 *
 * Copyright (C) 2007 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.jetbrains.letsPlot.commons.intern.gcommon.collect

abstract class Ordering<T> : Comparator<T> {

    fun isOrdered(iterable: Iterable<T>): Boolean {
        val it = iterable.iterator()
        if (it.hasNext()) {
            var prev: T = it.next()
            while (it.hasNext()) {
                val next = it.next()
                if (compare(prev, next) > 0) {
                    return false
                }
                prev = next
            }
        }
        return true
    }

    /**
     * @return immutable sorted list
     */
    fun <E : T> sortedCopy(elements: Iterable<E>): List<E> {
        return elements.sortedWith(object : Comparator<E> {
            override fun compare(a: E, b: E): Int {
                return this@Ordering.compare(a, b)
            }
        })
    }

    fun reverse(): Ordering<T> {
        return ComparatorOrdering(reversed())
    }

    fun <E : T> min(a: E, b: E): E {
        return if (compare(a, b) <= 0) a else b
    }

    fun <E : T> min(iterable: Iterable<E>): E {
        return min(iterable.iterator())
    }

    fun <E : T> min(iterator: Iterator<E>): E {
        // input must not be empty
        var result = iterator.next()
        while (iterator.hasNext()) {
            result = min(result, iterator.next())
        }

        return result
    }


    fun <E : T> max(a: E, b: E): E {
        return if (compare(a, b) >= 0) a else b
    }

    fun <E : T> max(iterable: Iterable<E>): E {
        return max(iterable.iterator())
    }

    fun <E : T> max(iterator: Iterator<E>): E {
        // input must not be empty
        var result = iterator.next()
        while (iterator.hasNext()) {
            result = max(result, iterator.next())
        }

        return result
    }

    companion object {

        fun <T> from(comparator: Comparator<T>): Ordering<T> {
            return if (comparator is Ordering<*>)
                comparator as Ordering<T>
            else
                ComparatorOrdering(comparator)
        }

        fun <T : Comparable<T>> natural(): Ordering<T> {
            return ComparatorOrdering(naturalOrder())
        }
    }

}
