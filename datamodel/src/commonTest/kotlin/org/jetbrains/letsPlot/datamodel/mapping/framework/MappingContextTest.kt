/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.mapping.framework

import org.jetbrains.letsPlot.commons.intern.function.Value
import kotlin.test.*

class MappingContextTest {

    private val context = MappingContext()

    @Test
    fun registerNonFindableMapper() {
        val mapper = createNonFindableMapper()

        val mapperRegistered = Value(false)
        context.addListener(object : MappingContextListener {
            override fun onMapperRegistered(mapper: Mapper<*, *>) {
                mapperRegistered.set(true)
            }

            override fun onMapperUnregistered(mapper: Mapper<*, *>) {}
        })

        mapper.attach(context)

        assertTrue(mapperRegistered.get())
    }

    @Test
    fun unregisterNonFindableMapper() {
        val mapper = createNonFindableMapper()
        mapper.attach(context)

        val mapperUnregistered = Value(false)
        context.addListener(object : MappingContextListener {
            override fun onMapperRegistered(mapper: Mapper<*, *>) {}

            override fun onMapperUnregistered(mapper: Mapper<*, *>) {
                mapperUnregistered.set(true)
            }
        })

        mapper.detach()

        assertTrue(mapperUnregistered.get())
    }

    @Test
    fun unknownPropertyFails() {
        assertFailsWith<IllegalStateException> {
            context.get(TEST)
        }
    }

    @Test
    fun containsProperty() {
        assertFalse(context.contains(TEST))
        context.put(TEST, "value")
        assertTrue(context.contains(TEST))
    }

    @Test
    fun removeUnknownProperty() {
        assertFailsWith<IllegalStateException> {
            context.remove(TEST)
        }
    }

    @Test
    fun removeProperty() {
        context.put(TEST, "value")
        assertEquals("value", context.remove(TEST))
        assertFalse(context.contains(TEST))
    }

    @Test
    fun putGet() {
        context.put(TEST, "value")
        assertEquals("value", context.get(TEST))
    }

    @Test
    fun putAllowedOnce() {
        assertFailsWith<IllegalStateException> {
            context.put(TEST, "value")
            context.put(TEST, "another value")
        }
    }

    @Test
    fun nullNotAllowed() {
        assertFailsWith<IllegalArgumentException> {
            context.put(TEST, null)
        }
    }

    private fun createNonFindableMapper(): ItemMapper {
        return object : ItemMapper(Item()) {
            override val isFindable: Boolean
                get() = false
        }
    }

    companion object {
        private val TEST: MappingContextProperty<Any> = MappingContextProperty("test")
    }
}