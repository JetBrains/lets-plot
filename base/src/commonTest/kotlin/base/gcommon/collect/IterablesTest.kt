package jetbrains.datalore.base.gcommon.collect

import jetbrains.datalore.base.assertion.assertFails
import jetbrains.datalore.base.function.Predicate
import kotlin.test.*

class IterablesTest {

    @Test
    fun get() {
        val iterable = iterable("a", "b", "c")

        assertEquals("a", Iterables[iterable, 0])
        assertEquals("c", Iterables[iterable, 2])

        assertFails { Iterables[iterable, -1] }
        assertFails { Iterables[iterable, 3] }
    }

    @Test
    fun getWithDefault() {
        val iterable = iterable("a", "b", "c")

        assertEquals("a", Iterables[iterable, 0, null])
        assertEquals("c", Iterables[iterable, 2, null])
        assertEquals("c+", Iterables[iterable, 3, "c+"])
        assertNull(Iterables[iterable, 3, null])

        assertFails { Iterables[iterable, -1] }
    }

    @Test
    fun filter() {
        val iterable = iterable("a", "b", "c")
        val filtered = Iterables.filter(iterable, eq("b"))
        assertEquals("b", Iterables[filtered, 0])
        assertFalse(Iterables.isEmpty(filtered))
    }

    @Test
    fun all() {
        val iterable: Iterable<String?> = iterable("a", "b", "c")
        assertTrue(Iterables.all(iterable) { it != null })
    }

    @Test
    fun find() {
        val iterable: Iterable<String?> = iterable("a", "b", "c")
        assertEquals("b", Iterables.find<String?>(iterable, eq("b"), null))
        assertEquals("b+", Iterables.find<String?>(iterable, eq("b+"), "b+"))
        assertNull(Iterables.find<String?>(iterable, eq("b-"), null))
    }

    @Test
    fun concat() {
        val a = iterable("a", "b")
        val b = iterable("b", "c")
        val result = Iterables.concat(a, b)

        assertEquals("a", Iterables[result, 0])
        assertEquals("b", Iterables[result, 1])
        assertEquals("b", Iterables[result, 2])
        assertEquals("c", Iterables[result, 3])
        assertFails { Iterables[result, 4] }
    }

    @Test
    fun getLast() {
        val iterable = iterable("a", "b", "c")
        assertEquals("c", Iterables.getLast(iterable))
        assertFails { Iterables.getLast(iterable()) }
    }

    @Test
    fun toArrayIt() {
        val array = Iterables.toArray(iterable("a", "b", "c"))
        assertTrue(arrayOf<Any>("a", "b", "c") contentEquals array)
    }

    @Test
    fun toArrayLi() {
        val array = Iterables.toArray(listOf("a", "b", "c"))
        assertTrue(arrayOf<Any>("a", "b", "c") contentEquals array)
    }

    companion object {
        internal fun iterable(vararg v: String): Iterable<String> {
            return object : Iterable<String> {
                private val myList = listOf(*v)

                override fun iterator(): Iterator<String> {
                    return myList.iterator()
                }
            }
        }

        private fun <T> eq(v: T): Predicate<in T> {
            return { v == it }
        }
    }
}