/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.gcommon.collect

import org.jetbrains.letsPlot.commons.intern.function.Predicate

class IterablesTest {

    companion object {
        internal fun iterable(vararg v: String): Iterable<String> {
            return object : Iterable<String> {
                private val myList = listOf(*v)

                override fun iterator(): Iterator<String> {
                    return myList.iterator()
                }
            }
        }

        private fun <T> eq(v: T): Predicate<T> {
            return { v == it }
        }
    }
}