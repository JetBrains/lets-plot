/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.gcommon.collect

import kotlin.test.Test
import kotlin.test.assertEquals

class TreeMapTest {

    @Test
    fun floorAndCeilingKeys() {
        val map = treeMapOf(
            6 to "ddd",
            2 to "aaa",
            5 to "ccc",
            4 to "bbb"
        )

        assertEquals(5, map.floorKey(5))
        assertEquals(5, map.ceilingKey(5))

        assertEquals(2, map.floorKey(3))
        assertEquals(4, map.ceilingKey(3))

        assertEquals(null, map.floorKey(1))
        assertEquals(2, map.ceilingKey(1))

        assertEquals(6, map.floorKey(7))
        assertEquals(null, map.ceilingKey(7))
    }

    companion object {
        internal fun <K : Comparable<K>, V> treeMapOf(vararg pairs: Pair<K, V>): TreeMap<K, V> {
            val result = TreeMap<K, V>()
            for ((k, v) in pairs) {
                result.put(k, v)
            }
            return result
        }
    }
}