/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.mapping.framework

import org.assertj.core.api.AbstractObjectAssert
import org.assertj.core.api.Assertions.assertThat
import java.util.Collections.addAll
import kotlin.test.Test
import kotlin.test.fail

class RemoveFailedChildTest {
    @Test
    fun property() {
        val root = object : RootMapper() {
            override fun onAttach(ctx: MappingContext) {
                property.set(BadMapper())
            }
        }

        attachExpectingException(root)

        val removed = root.property.get()!!
        root.property.set(GoodMapper())

        assertThatMapper(removed)
                .isBad
                .isNotInContext(root)
    }

    @Test
    fun list() {
        val root = object : RootMapper() {
            override fun onAttach(ctx: MappingContext) {
                addAll(list, GoodMapper(), BadMapper(), GoodMapper())
            }
        }

        attachExpectingException(root)

        val removed = root.list.removeAt(1)
        assertThatMapper(removed)
                .isBad
                .isNotInContext(root)
    }

    @Test
    fun set() {
        val root = object : RootMapper() {
            override fun onAttach(ctx: MappingContext) {
                addAll<Mapper<Any, Any>>(set, GoodMapper(), BadMapper())
            }
        }

        attachExpectingException(root)

        var removed: BadMapper? = null
        val i = root.set.iterator()
        while (i.hasNext()) {
            val next = i.next()
            if (next is BadMapper) {
                removed = next
                i.remove()
                break
            }
        }

        assertThatMapper(removed!!)
                .isNotNull
                .isNotInContext(root)
    }

    private fun attachExpectingException(mapper: Mapper<*, *>) {
        try {
            mapper.attachRoot()
            fail(BadSynchronizerException::class.java.simpleName + " expected")
        } catch (ignored: BadSynchronizerException) {
        }

    }

    private fun assertThatMapper(mapper: Mapper<Any, Any>): MapperAssert {
        return MapperAssert(mapper)
    }

    private open class RootMapper internal constructor() : Mapper<Any, Any>(Any(), Any()) {
        internal val property = createChildProperty<Mapper<Any, Any>>()
        internal val list = createChildList<Mapper<Any, Any>>()
        internal val set = createChildSet<Mapper<Any, Any>>()
    }

    private class BadMapper internal constructor() : Mapper<Any, Any>(Any(), Any()) {

        override fun registerSynchronizers(conf: SynchronizersConfiguration) {
            super.registerSynchronizers(conf)
            conf.add(object : Synchronizer {
                override fun attach(ctx: SynchronizerContext) {
                    throw BadSynchronizerException()
                }

                override fun detach() {}
            })
        }
    }

    private class BadSynchronizerException : RuntimeException()

    private class GoodMapper : Mapper<Any, Any>(Any(), Any())

    private class MapperAssert(mapper: Mapper<Any, Any>) :
            AbstractObjectAssert<MapperAssert, Mapper<Any, Any>>(mapper, MapperAssert::class.java) {

        internal val isBad: MapperAssert
            get() {
                val actual = actual
                assertThat(actual)
                        .isInstanceOf(BadMapper::class.java)
                return this
            }

        internal fun isNotInContext(parent: Mapper<Any, Any>): MapperAssert {
            assertThat(parent.getDescendantMapper(actual.source))
                    .isNull()
            assertThat(parent.mappingContext!!.getMappers().contains(actual))
                    .isFalse()
            return this
        }
    }
}
