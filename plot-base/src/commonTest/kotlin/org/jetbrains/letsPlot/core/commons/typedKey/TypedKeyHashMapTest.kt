/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.commons.typedKey

import kotlin.test.Test
import kotlin.test.assertFalse


class TypedKeyHashMapTest {
    private fun <T> create(name: String): Key<T> {
        return BadKey(name)
    }

    @Test
    fun badKey() {
        val typedKeyMap = TypedKeyHashMap()
        val stringListTypedKey = create<List<String>>("stringList")
        val integerListTypedKey = create<List<Int>>("integerList")
        val stringList = listOf("a", "b")
        typedKeyMap.put(stringListTypedKey, stringList)

        // Redefine from List<Int> to List<Any>
        val integerList: List<Any> = typedKeyMap[integerListTypedKey]
        // With `integerList: List<Int>` it will be cast to Int, throwing ClassCastException
        val firstInteger = integerList[0]  // as Any - added implicitly by compiler because of List<Any> type
        assertFalse(firstInteger is Int)
    }

    private open class Key<T>(private val myName: String) : TypedKey<T> {
        override fun toString(): String {
            return myName
        }
    }

    private class BadKey<T>(name: String) : Key<T>(name) {
        override fun hashCode(): Int {
            return 0
        }

        override fun equals(other: Any?): Boolean {
            return true
        }
    }
}
