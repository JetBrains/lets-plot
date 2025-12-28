/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.shape

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import kotlin.test.Test
import kotlin.test.assertEquals

class PropertiesSynchronizationTest {

    @Test
    fun `transitive dep baz in foo-bar-baz should be updated on foo invalidation`() {
        class C :   Element() {
            var foo: String by visualProp("")
            val bar: String by computedProp(C::foo) { foo + "bar" }
            val baz: String by computedProp(C::bar) { bar + "baz" }
            override fun calculateLocalBBox(): DoubleRectangle {
                TODO("Not yet implemented")
            }
        }

        val c = C()
        assertEquals("barbaz", c.baz)

        c.foo = "foo"
        // DO NOT READ intermediate props - this will update transitive dependencies
        assertEquals("foobarbaz", c.baz)
    }

    @Test
    fun `transitive dep e in a-b-c-d-e should be updated on a invalidation`() {
        class C : Element() {
            var a: String by visualProp("")
            val b: String by computedProp(C::a) { a + "b" }
            val c: String by computedProp(C::b) { b + "c" }
            val d: String by computedProp(C::c) { c + "d" }
            val e: String by computedProp(C::d) { d + "e" }
            override fun calculateLocalBBox(): DoubleRectangle {
                TODO("Not yet implemented")
            }
        }

        val c = C()
        assertEquals("bcde", c.e)

        c.a = "a"
        // DO NOT READ intermediate props - this will update transitive dependencies
        assertEquals("abcde", c.e)
    }

    @Test
    fun `two properties with same dep`() {
        class C : Element() {
            var a: String by visualProp("")
            val shared: String by computedProp(C::a) { a + "shared" }
            val c: String by computedProp(C::shared) { shared + "c" }
            val d: String by computedProp(C::shared) { shared + "d" }
            override fun calculateLocalBBox(): DoubleRectangle {
                TODO("Not yet implemented")
            }
        }

        val c = C()
        assertEquals("sharedc", c.c)
        assertEquals("sharedd", c.d)

        c.a = "a"
        // DO NOT READ intermediate props - this will update transitive dependencies
        assertEquals("asharedc", c.c)
        assertEquals("asharedd", c.d)
    }

    @Test(expected = IllegalStateException::class)
    fun `cyclic deps are not allowed`() {
        class C : Element() {
            var foo: String by visualProp("")
            val bar: String by computedProp(C::foo, C::baz) { foo + "bar" }
            val baz: String by computedProp(C::foo, C::bar) { bar + "baz" }
            override fun calculateLocalBBox(): DoubleRectangle {
                TODO("Not yet implemented")
            }
        }

        C() // java.lang.IllegalStateException: Missing dependency: baz. All dependencies must be defines before
    }
}