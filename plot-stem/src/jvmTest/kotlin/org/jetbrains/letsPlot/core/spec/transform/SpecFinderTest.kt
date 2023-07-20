/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.transform

import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(Parameterized::class)
class SpecFinderTest(private val myInput: Map<String, Any>, private val myKeys: Array<String>, private val myExpected: List<Map<String, Any>>) {

    @Test
    fun findSpec() {
        val finder = SpecFinder(*myKeys)
        val output = finder.findSpecs(myInput)
        assertEquals(myExpected, output)
    }

    companion object {
        private val TARGET_SPEC = mapOf("target-key" to "target-val")
        private val TARGET_LIST_OF_ONE = listOf(TARGET_SPEC)


        @JvmStatic
        @Parameterized.Parameters
        fun params(): Collection<Array<Any>> {

            val listContainingSpecsContainingTargetSpecs = list(
                    map(TARGET_SPEC),
                    "_",
                    map(TARGET_SPEC),
                    "_",
                    map(TARGET_SPEC)
            )

            // this is the same structure as plot spec:
            // {
            //   LAYERS = [
            //              { layer spec },
            //              { layer spec },
            //              ... layer specs ...
            //            ]
            // }
            val listContainingTargetSpecs = list(
                    TARGET_SPEC,
                    "_",
                    TARGET_SPEC,
                    "_",
                    TARGET_SPEC
            )

            return listOf(arrayOf(emptyMap<Any, Any>(), arrayOf("a", "b", "c"), emptyList<Any>()), arrayOf(emptyMap<Any, Any>(), arrayOfNulls<String>(0), listOf(emptyMap<Any, Any>())), arrayOf(
                TARGET_SPEC, arrayOfNulls<String>(0), TARGET_LIST_OF_ONE
            ), arrayOf(map(TARGET_SPEC, "_", "_"), arrayOf("0"), TARGET_LIST_OF_ONE), arrayOf(map("_", TARGET_SPEC, "_"), arrayOf("1"), TARGET_LIST_OF_ONE), arrayOf(
                map("_", "_", TARGET_SPEC), arrayOf("2"), TARGET_LIST_OF_ONE
            ), arrayOf(map(TARGET_SPEC, TARGET_SPEC, TARGET_SPEC), arrayOf("2"), TARGET_LIST_OF_ONE), arrayOf(
                map(
                    map(map("_", "_", TARGET_SPEC))
                ), arrayOf("0", "0", "2"), TARGET_LIST_OF_ONE
            ), arrayOf(map(list("_", map("_", "_", TARGET_SPEC), "_")), arrayOf("0", "2"), TARGET_LIST_OF_ONE), arrayOf(
                map(listContainingSpecsContainingTargetSpecs), arrayOf("0", "0"), listOf<Map<out Any, Any>>(TARGET_SPEC, TARGET_SPEC, TARGET_SPEC)), arrayOf(
                map(listContainingTargetSpecs), arrayOf("0"), listOf<Map<out Any, Any>>(TARGET_SPEC, TARGET_SPEC, TARGET_SPEC)))
        }

        private fun map(vararg values: Any): Map<*, *> {
            val m = HashMap<String, Any>()
            for ((index, value) in values.withIndex()) {
                m["" + index] = value
            }
            return ImmutableMap(m)
        }

        private fun list(vararg values: Any): List<*> {
            return listOf(*values)
        }

        private class ImmutableMap<K, V>(private val inner: Map<K, V>) : Map<K, V> by inner
    }
}