/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.core

import org.jetbrains.letsPlot.livemap.containers.LruCache
import kotlin.math.min
import kotlin.test.*
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class LruCacheTest {

    @Test
    fun getOrPutTest() {
        val limit = 3
        val list = listOf(1, 2, 3)
        val cache = LruCache<Int, Int>(limit)
        list.forEach { cache.put(it, it) }

        cache.getOrPut(2) { 4 }
        cache.getOrPut(5) { 5 }

        assertEquals(listOf(5, 2, 3), cache.values)
    }

    @Test
    fun putSameKeyTest() {
        val limit = 3
        val list = listOf(1, 2, 3)
        val cache = LruCache<Int, Int>(limit)
        list.forEach { cache.put(it, it) }

        cache.put(1, 1)
        cache.put(4, 4)

        assertEquals(listOf(4, 1, 3), cache.values)
    }

    @Test
    fun getHeadTest() {
        val limit = 3
        val list = listOf(7, 8, 9)
        val cache = LruCache<String, Int>(limit)
        list.forEach { cache.put(it.toString(), it) }

        cache["9"]

        assertEquals(listOf(9, 8, 7), cache.values)
    }

    @Test
    fun getTailTest() {
        val limit = 3
        val list = listOf(7, 8, 9)
        val cache = LruCache<String, Int>(limit)
        list.forEach { cache.put(it.toString(), it) }

        cache["7"]

        assertEquals(listOf(7, 9, 8), cache.values)
    }

    @Test
    fun limitOneTest() {
        val limit = 1
        val list = listOf(5, 6, 7, 8, 9)
        val cache = LruCache<String, Int>(limit)
        list.forEach { cache.put(it.toString(), it) }

        cache["7"]

        assertEquals(listOf(9), cache.values)
    }

    @Test
    fun limitOneGetNullTest() {
        val limit = 1
        val list = listOf(5, 6, 7, 8, 9)
        val cache = LruCache<String, Int>(limit)
        list.forEach { cache.put(it.toString(), it) }

        assertNull(cache["7"])

        assertEquals(listOf(9), cache.values)
    }

    @Test
    fun limitOneGetNotNullTest() {
        val limit = 1
        val list = listOf(5, 6, 7, 8, 9)
        val cache = LruCache<String, Int>(limit)
        list.forEach { cache.put(it.toString(), it) }

        assertNotNull(cache["9"])

        assertEquals(listOf(9), cache.values)
    }

    @Test
    fun cacheTest() {
        val limit = 3
        val list = listOf(1,2,3,4,5,6,7,8,9)

        val cache = LruCache<String, Int>(limit)

        cache.apply {
            list.forEach { put(it.toString(), it) }
        }

        cache["8"]

        assertEquals(listOf(8, 9, 7), cache.values)
    }

    @Test
    fun getByEmptyTest() {
        val cache = LruCache<String, Int>(1)
        val nullResult = cache["missing"]

        assertEquals(null, nullResult)
    }

    @Ignore // Slow test
    @ExperimentalTime
    @Test
    fun stressTest() {
        val count = 1000000

        measure(5) {
            val cache = LruCache<String, Int>(count)
            repeat(count) {
                cache.put(it.toString(), it)
            }
        }.let(::println)
    }

    @Ignore // Slow test
    @ExperimentalTime
    @Test
    fun stressHalfLimitTest() {
        val count = 1000000

        measure(5) {
            val cache = LruCache<String, Int>(count / 2)
            repeat(count) {
                cache.put(it.toString(), it)
            }
        }.let(::println)
    }

    @Ignore // Slow test
    @ExperimentalTime
    @Test
    fun stressDuplicatedElementsTest() {
        val count = 1_000_000

        measure(5) {
            val cache = LruCache<String, Int>(count)
            repeat(count) {
                cache.put(1.toString(), it)
            }
        }.let(::println)
    }

    @Ignore // Slow test
    @ExperimentalTime
    @Test
    fun stressGetTest() {
        val count = 1000000
        val cache = LruCache<String, Int>(count)
        repeat(count) {
            cache.put(it.toString(), it)
        }

        measure(5) {
            repeat(count) {
                cache[it.toString()]
            }
        }.let(::println)
    }


    @ExperimentalTime
    fun measure(n: Int, f: () -> Unit): Double {
        var minTime: Double = Double.MAX_VALUE
        repeat(n) {
            minTime = min(measureTime(f).toDouble(DurationUnit.MILLISECONDS), minTime)
        }
        return minTime
    }
}