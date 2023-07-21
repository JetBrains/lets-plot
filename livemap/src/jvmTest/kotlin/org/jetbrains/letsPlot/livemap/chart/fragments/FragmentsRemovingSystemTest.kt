/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.chart.fragments

import org.jetbrains.letsPlot.commons.intern.spatial.LonLat
import org.jetbrains.letsPlot.commons.intern.spatial.QuadKey
import org.jetbrains.letsPlot.livemap.Mocks
import org.jetbrains.letsPlot.livemap.Utils
import org.jetbrains.letsPlot.livemap.chart.fragment.CachedFragmentsComponent
import org.jetbrains.letsPlot.livemap.chart.fragment.ChangedFragmentsComponent
import org.jetbrains.letsPlot.livemap.chart.fragment.FragmentsRemovingSystem
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestName
import java.lang.reflect.Method

class FragmentsRemovingSystemTest : RegionsTestBase() {
    @get:Rule
    var testName = TestName()
    private lateinit var fragmentFoo1: FragmentSpec
    private lateinit var fragmentFoo2: FragmentSpec
    private lateinit var fragmentFoo3: FragmentSpec
    private lateinit var fragmentFoo4: FragmentSpec
    override fun setUp() {
        super.setUp()
        createEntity("FragmentsChange", ChangedFragmentsComponent())
        createEntity("FragmentsState", CachedFragmentsComponent())
        fragmentFoo1 = fragmentSpecWithGeometry(FOO_REGION_ID, QUAD_1)
        fragmentFoo2 = fragmentSpecWithGeometry(FOO_REGION_ID, QUAD_2)
        fragmentFoo3 = fragmentSpecWithGeometry(FOO_REGION_ID, QUAD_3)
        fragmentFoo4 = fragmentSpecWithGeometry(FOO_REGION_ID, QUAD_4)
        val cacheSize = readCacheSizeAnnotation()
        addSystem(FragmentsRemovingSystem(cacheSize, componentManager))
    }

    override val systemsOrder
        get() = listOf(FragmentsRemovingSystem::class)

    @Test
    fun simpleWithCacheSize5() {
        update(
            Mocks.cachedFragments(this).add(fragmentFoo1, fragmentFoo2)
        )
        assertThatFragment(fragmentFoo1).isReady
        assertThatFragment(fragmentFoo2).isReady
        update(
            Mocks.changedFragments(this)
                .obsolete(fragmentFoo1, fragmentFoo2)
                .requested(fragmentFoo3, fragmentFoo4)
        )
        assertThatFragment(fragmentFoo1).isReady
        assertThatFragment(fragmentFoo2).isReady
    }

    private fun assertThatFragment(spec: FragmentSpec?): FragmentAssert {
        return FragmentAssert(spec, this)
    }

    private fun readCacheSizeAnnotation(): Int {
        val thisTest: Method
        thisTest = try {
            this.javaClass.getDeclaredMethod(testName.methodName)
        } catch (e: SecurityException) {
            throw RuntimeException(e)
        } catch (e: NoSuchMethodException) {
            throw RuntimeException(e)
        }
        return if (thisTest.isAnnotationPresent(CacheSize::class.java)) {
            thisTest.getAnnotation(CacheSize::class.java).size
        } else DEFAULT_CACHE_SIZE
    }

    @Retention(AnnotationRetention.RUNTIME)
    @Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
    internal annotation class CacheSize(val size: Int)
    companion object {
        private const val DEFAULT_CACHE_SIZE = 2
        private const val FOO_REGION_ID = "123123"
        private val QUAD_1: QuadKey<LonLat> = Utils.quad("1")
        private val QUAD_2: QuadKey<LonLat> = Utils.quad("2")
        private val QUAD_3: QuadKey<LonLat> = Utils.quad("3")
        private val QUAD_4: QuadKey<LonLat> = Utils.quad("4")
    }
}