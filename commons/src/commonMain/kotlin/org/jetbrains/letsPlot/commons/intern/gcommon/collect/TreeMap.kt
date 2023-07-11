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

package org.jetbrains.letsPlot.commons.intern.gcommon.collect

class TreeMap<K : Comparable<K>, V> {
    val values: Collection<V>
        get() = map.values
    private val sortedKeys: MutableList<K> = ArrayList()
    private val map: MutableMap<K, V> = HashMap()

    operator fun get(key: K): V? = map[key]

    fun put(key: K, value: V): V? {
        val index = sortedKeys.binarySearch(key)
        if (index < 0) {
            sortedKeys.add(index.inv(), key)
        } else {
            sortedKeys[index] = key
        }
        return map.put(key, value)
    }

    fun containsKey(key: K): Boolean = map.containsKey(key)

    fun floorKey(key: K): K? {
        var index = sortedKeys.binarySearch(key)

        if (index < 0) {
            index = index.inv() - 1

            if (index < 0)
                return null
        }
        return sortedKeys[index]
    }

    fun ceilingKey(key: K): K? {
        var index = sortedKeys.binarySearch(key)

        if (index < 0) {
            index = index.inv()

            if (index == sortedKeys.size)
                return null
        }
        return sortedKeys[index]
    }
}
