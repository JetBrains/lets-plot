/*
 * Copyright (c) 2019 JetBrains s.r.o.
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

package jetbrains.datalore.base.gcommon.collect

object Sets {
    /**
     * Mutable set
     */
    fun <E> newHashSet(elements: Iterable<E>): MutableSet<E> {
        if (elements is Collection<*>) {
            val collection = elements as Collection<E>
            return HashSet(collection)
        }
        return newHashSet(elements.iterator())
    }

    /**
     * Mutable set
     */
    private fun <E> newHashSet(elements: Iterator<E>): MutableSet<E> {
        val set = HashSet<E>()
        while (elements.hasNext()) {
            set.add(elements.next())
        }
        return set
    }

//    /**
//     * Unmodifiable copy
//     */
//    fun <E> difference(set1: Set<E>, set2: Set<E>): Set<E> {
//        val copy = HashSet(set1)
//        copy.removeAll(set2)
//        return copy
//    }
}
