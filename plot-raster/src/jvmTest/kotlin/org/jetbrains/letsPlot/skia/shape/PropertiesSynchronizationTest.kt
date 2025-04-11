/*
 * Copyright (c) 2024 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.shape

import org.jetbrains.letsPlot.raster.shape.Node
import kotlin.test.Test
import kotlin.test.assertEquals

class PropertiesSynchronizationTest {

    @Test
    fun `transitive dep baz in foo-bar-baz should be updated on foo invalidation`() {
        class C : Node() {
            var foo: String by visualProp("")
            val bar: String by computedProp(C::foo) { foo + "bar" }
            val baz: String by computedProp(C::bar) { bar + "baz" }
        }

        val c = C()
        assertEquals("barbaz", c.baz)

        c.foo = "foo"
        // DO NOT READ intermediate props - this will update transitive dependencies
        assertEquals("foobarbaz", c.baz)
    }

    @Test
    fun `transitive dep e in a-b-c-d-e should be updated on a invalidation`() {
        class C : Node() {
            var a: String by visualProp("")
            val b: String by computedProp(C::a) { a + "b" }
            val c: String by computedProp(C::b) { b + "c" }
            val d: String by computedProp(C::c) { c + "d" }
            val e: String by computedProp(C::d) { d + "e" }
        }

        val c = C()
        assertEquals("bcde", c.e)

        c.a = "a"
        // DO NOT READ intermediate props - this will update transitive dependencies
        assertEquals("abcde", c.e)
    }

    @Test
    fun `two properties with same dep`() {
        class C : Node() {
            var a: String by visualProp("")
            val shared: String by computedProp(C::a) { a + "shared" }
            val c: String by computedProp(C::shared) { shared + "c" }
            val d: String by computedProp(C::shared) { shared + "d" }
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
        class C : Node() {
            var foo: String by visualProp("")
            val bar: String by computedProp(C::foo, C::baz) { foo + "bar" }
            val baz: String by computedProp(C::foo, C::bar) { bar + "baz" }
        }

        C() // java.lang.IllegalStateException: Missing dependency: baz. All dependencies must be defines before
    }
}