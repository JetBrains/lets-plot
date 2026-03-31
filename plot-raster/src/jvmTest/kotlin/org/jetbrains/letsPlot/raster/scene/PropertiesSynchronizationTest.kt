/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.scene

import kotlin.test.Test
import kotlin.test.assertEquals

class PropertiesSynchronizationTest {

    @Test
    fun `transitive dep baz in foo-bar-baz should be updated on foo invalidation`() {
        val attributeRegistry = AttributeRegistry()
        class C : AttributeObject() {
            override val attributeRegistry: AttributeRegistry get() = attributeRegistry
            override val parent: AttributeObject? = null
            override fun onAttributeChangedInternal(attrSpec: AttributeSpec, oldValue: Any?, newValue: Any?) { }

            init {
                val META = attributeRegistry.addClass(C::class)
                val FooAttrSpec = META.registerVariableAttr(C::foo)
                val BarAttrSpec = META.registerDerivedAttr(C::bar, setOf(FooAttrSpec))
                val BazAttrSpec = META.registerDerivedAttr(C::baz, setOf(BarAttrSpec))
            }

            var foo: String by variableAttr("")
            val bar: String by derivedAttr { foo + "bar" }
            val baz: String by derivedAttr { bar + "baz" }
        }

        val c = C()
        assertEquals("barbaz", c.baz)

        c.foo = "foo"
        // DO NOT READ intermediate props - this will update transitive dependencies
        assertEquals("foobarbaz", c.baz)
    }

    @Test
    fun `transitive dep e in a-b-c-d-e should be updated on a invalidation`() {
        val attributeRegistry = AttributeRegistry()
        class C : AttributeObject() {
            override val attributeRegistry: AttributeRegistry get() = attributeRegistry
            override val parent: AttributeObject? = null
            override fun onAttributeChangedInternal(attrSpec: AttributeSpec, oldValue: Any?, newValue: Any?) { }

            init {
                val META = attributeRegistry.addClass(C::class)

                val AAttrSpec = META.registerVariableAttr(C::a)
                val BAttrSpec = META.registerDerivedAttr(C::b, setOf(AAttrSpec))
                val CAttrSpec = META.registerDerivedAttr(C::c, setOf(BAttrSpec))
                val DAttrSpec = META.registerDerivedAttr(C::d, setOf(CAttrSpec))
                val EAttrSpec = META.registerDerivedAttr(C::e, setOf(DAttrSpec))
            }

            var a: String by variableAttr("")
            val b: String by derivedAttr { a + "b" }
            val c: String by derivedAttr { b + "c" }
            val d: String by derivedAttr { c + "d" }
            val e: String by derivedAttr { d + "e" }
        }

        val c = C()
        assertEquals("bcde", c.e)

        c.a = "a"
        // DO NOT READ intermediate props - this will update transitive dependencies
        assertEquals("abcde", c.e)
    }

    @Test
    fun `two properties with same dep`() {
        val attributeRegistry = AttributeRegistry()
        class C : AttributeObject() {
            override val attributeRegistry: AttributeRegistry get() = attributeRegistry
            override val parent: AttributeObject? = null
            override fun onAttributeChangedInternal(attrSpec: AttributeSpec, oldValue: Any?, newValue: Any?) { }

            init {
                val META = attributeRegistry.addClass(C::class)

                val AAttrSpec = META.registerVariableAttr(C::a)
                val SharedAttrSpec = META.registerDerivedAttr(C::shared, setOf(AAttrSpec))
                val CAttrSpec = META.registerDerivedAttr(C::c, setOf(SharedAttrSpec))
                val DAttrSpec = META.registerDerivedAttr(C::d, setOf(SharedAttrSpec))
            }

            var a: String by variableAttr("")
            val shared: String by derivedAttr { a + "shared" }
            val c: String by derivedAttr { shared + "c" }
            val d: String by derivedAttr { shared + "d" }
        }

        val c = C()
        assertEquals("sharedc", c.c)
        assertEquals("sharedd", c.d)

        c.a = "a"
        // DO NOT READ intermediate props - this will update transitive dependencies
        assertEquals("asharedc", c.c)
        assertEquals("asharedd", c.d)
    }
}
