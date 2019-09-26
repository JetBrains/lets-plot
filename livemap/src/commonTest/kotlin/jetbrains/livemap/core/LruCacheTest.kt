package jetbrains.livemap.core

import jetbrains.livemap.containers.LruCache
import kotlin.test.Test
import kotlin.test.assertEquals

class LruCacheTest {

    @Test
    fun cacheTest() {
        val limit = 3
        val list = listOf(1,2,3,4,5,6,7,8,9)

        val cache = LruCache<String, Int>(limit)

        cache.apply {
            list.forEach { put(it.toString(), it) }
        }

        cache["8"]

        assertEquals(listOf(8,9,7), cache.values)
    }

    @Test
    fun getByEmptyTest() {
        val cache = LruCache<String, Int>(1)
        val nullResult = cache["missing"]

        assertEquals(null, nullResult)
    }
}