/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.mapper.core

import jetbrains.datalore.base.observable.collections.list.ObservableArrayList
import jetbrains.datalore.base.observable.collections.list.ObservableList
import kotlin.test.Test
import kotlin.test.assertEquals

class TransformingSynchronizerTest {
    private lateinit var source: ObservableList<String>
    private lateinit var target: ObservableList<String>

    private fun init(vararg items: String) {
        source = ObservableArrayList()
        source.addAll(items)
        val mapper = MyMapper(source)
        mapper.attachRoot()
        target = mapper.target
    }

    @Test
    fun initialMapping() {
        init("z", "c", "b")

        assertTarget("b", "c", "z")
    }

    @Test
    fun add() {
        init("a", "c")

        source.add("b")

        assertTarget("a", "b", "c")
    }

    @Test
    fun remove() {
        init("a", "c", "b")

        source.removeAt(1)

        assertTarget("a", "b")
    }

    private fun assertTarget(vararg items: String) {
        assertEquals(listOf(*items), target)
    }

    internal class MyMapper(source: ObservableList<String>) : Mapper<ObservableList<String>, ObservableList<String>>(source, ObservableArrayList()) {

        override fun registerSynchronizers(conf: SynchronizersConfiguration) {
            super.registerSynchronizers(conf)

            conf.add(TransformingObservableCollectionRoleSynchronizer(this,
                    source,
                    Transformers.sortBy { value: String -> Properties.constant(value) },
                    target,
                    object : MapperFactory<String, String> {
                        override fun createMapper(source: String): Mapper<out String, out String> {
                            return object : Mapper<String, String>(source, source) {

                            }
                        }
                    }))
        }
    }
}