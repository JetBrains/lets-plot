package jetbrains.datalore.base.typedKey

import kotlin.test.Test
import kotlin.test.assertFalse


class TypedKeyContainerTest {
    private fun <T> create(name: String): Key<T> {
        return BadKey(name)
    }

    @Test
    fun badKey() {
        val typedKeyContainer = TypedKeyHashMap()
        val stringListTypedKey = create<List<String>>("stringList")
        val integerListTypedKey = create<List<Int>>("integerList")
        val stringList = listOf("a", "b")
        typedKeyContainer.put(stringListTypedKey, stringList)

        val integerList = typedKeyContainer[integerListTypedKey]
        val firstInteger: Any? = integerList[0]  // Class cast error if: String -> Int  (only when JVM)
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
